package com.smartcommunity.smart_community_platform.config.TicketSTateMachine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcommunity.smart_community_platform.dao.StateMachineRecordMapper;
import com.smartcommunity.smart_community_platform.model.entity.StateMachineRecord;
import com.smartcommunity.smart_community_platform.model.enums.TicketEvent;
import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import com.smartcommunity.smart_community_platform.service.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.statemachine.*;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.*;

@Configuration
@Slf4j
@EnableStateMachineFactory(name = "ticketStateMachineFactory")
@RequiredArgsConstructor
public class TicketStateMachineConfig
        extends StateMachineConfigurerAdapter<TicketState, TicketEvent> {

    private final AllocationService allocationService;
    private final TicketStateMachineListener stateMachineListener;


    @Override
    public void configure(StateMachineStateConfigurer<TicketState, TicketEvent> states) throws Exception {
        states.withStates()
                .initial(TicketState.CREATED)
                .states(EnumSet.allOf(TicketState.class))
                .end(TicketState.COMPLETED)
                .end(TicketState.CLOSED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<TicketState, TicketEvent> config) throws Exception {
        config
                .withConfiguration()
                .machineId("ticketMachine")
                .listener(stateMachineListener); // 注册全局监听器
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TicketState, TicketEvent> transitions) throws Exception {
        transitions
                // 自动分配转换
                .withExternal()
                .source(TicketState.CREATED)
                .target(TicketState.AUTO_ASSIGNED)
                .event(TicketEvent.AUTO_ASSIGN)
                .action(ctx -> allocationService.autoAssignWorker(
                        Long.parseLong(ctx.getStateMachine().getId())))
                .and()

                // 处理流程转换
                .withExternal()
                .source(TicketState.AUTO_ASSIGNED)
                .target(TicketState.PROCESSING)
                .event(TicketEvent.MANUAL_PROCESS)
                .and()

                // 提交审核（新增待审核状态）
                .withExternal()
                .source(TicketState.PROCESSING)
                .target(TicketState.REVIEW_PENDING)
                .event(TicketEvent.SUBMIT_REVIEW)
                .and()

                // 审核通过流程
                .withExternal()
                .source(TicketState.REVIEW_PENDING)
                .target(TicketState.REVIEW_PASSED)
                .event(TicketEvent.REVIEW_PASS)
                .guard(this::checkReviewResult)
                .and()

                // 审核拒绝流程
                .withExternal()
                .source(TicketState.REVIEW_PENDING)
                .target(TicketState.REVIEW_FAILED)
                .event(TicketEvent.REVIEW_REJECT)
                .and()

                // 用户确认流程
                .withExternal()
                .source(TicketState.REVIEW_PASSED)
                .target(TicketState.USER_CONFIRMED)
                .event(TicketEvent.USER_CONFIRM)
                .and()

                // 重新处理流程
                .withExternal()
                .source(TicketState.REVIEW_FAILED)
                .target(TicketState.PROCESSING)
                .event(TicketEvent.MANUAL_PROCESS)
                .and()

                // 完成状态转换
                .withExternal()
                .source(TicketState.USER_CONFIRMED)
                .target(TicketState.COMPLETED)
                .event(TicketEvent.USER_CONFIRM)
                .and()

                // 关闭工单流程
                .withExternal()
                .source(TicketState.REVIEW_FAILED)
                .target(TicketState.CLOSED)
                .event(TicketEvent.SYSTEM_CLOSE)
                .and()

                .withExternal()
                .source(TicketState.COMPLETED)
                .target(TicketState.CLOSED)
                .event(TicketEvent.SYSTEM_CLOSE);
    }


    @Bean
    public DefaultStateMachinePersister<TicketState, TicketEvent, String> stateMachinePersister(
            StateMachineRecordMapper recordMapper) {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<>() {
            private final ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public void write(StateMachineContext<TicketState, TicketEvent> context, String machineId) {
                StateMachineRecord record = recordMapper.selectByMachineId(machineId);
                try {
                    Map<String, Object> contextData = new HashMap<>();

                    // 安全处理事件头
                    Optional.ofNullable(context.getEventHeaders())
                            .orElseGet(Collections::emptyMap)
                            .forEach((key, value) -> contextData.put("header_"+key.toString(), value));

                    // 安全处理扩展变量
                    Optional.ofNullable(context.getExtendedState().getVariables())
                            .orElseGet(Collections::emptyMap)
                            .forEach((key, value) -> contextData.put(key.toString(), value));

                    log.info("write: 当前上下文，{}", contextData);

                    if (record == null) {
                        record = new StateMachineRecord();
                        record.setMachineId(machineId);
                        record.setState(context.getState());
                        record.setContextJson(objectMapper.writeValueAsString(contextData));
                        recordMapper.insert(record);

                        log.info("write: 从持久化获取为null，insert：{}", record);

                    } else {
                        record.setState(context.getState());
                        record.setContextJson(objectMapper.writeValueAsString(contextData));
                        int count = recordMapper.updateByMachineId(record);
                        log.info("write:更新持久化 insert：{}", record);
                        if (count == 0) {
                            throw new OptimisticLockingFailureException("状态机并发更新冲突");
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new StateMachineException("序列化失败", e);
                } catch (Exception e) {
                    throw new StateMachineException("持久化失败", e);
                }
            }

            // TicketStateMachineConfig.java
            @Override
            public StateMachineContext<TicketState, TicketEvent> read(String machineId) {
                try {
                    StateMachineRecord record = recordMapper.selectByMachineId(machineId);

                    // 分离事件头和扩展变量
                    Map<String, Object> eventHeaders = new HashMap<>();
                    Map<Object, Object> extendedVariables = new HashMap<>();

                    if (record != null && record.getContextJson() != null) {
                        Map<String, Object> context = objectMapper.readValue(
                                record.getContextJson(),
                                new TypeReference<Map<String, Object>>() {
                                }
                        );

                        // 按前缀分离数据
                        context.forEach((key, value) -> {
                            if (key.startsWith("header_")) {
                                eventHeaders.put(key.substring(7), value);
                            } else {
                                extendedVariables.put(key, value);
                            }
                        });
                    }

                    // 构建扩展状态对象
                    ExtendedState extendedState = new DefaultExtendedState();
                    extendedState.getVariables().putAll(extendedVariables);

                    return new DefaultStateMachineContext<>(
                            record != null ? record.getState() : TicketState.CREATED,
                            null,
                            eventHeaders,
                            extendedState,  // 正确设置扩展状态
                            null,
                            machineId
                    );
                } catch (Exception e) {
                    throw new StateMachineException("恢复状态失败", e);
                }
            }


//=============================================
        });
    }

    private Map<String, Object> deserializeContext(String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("状态机上下文反序列化失败", e);
        }
    }

    private boolean checkReviewResult(StateContext<TicketState, TicketEvent> ctx) {
        return ctx.getMessageHeaders().get("reviewResult", Boolean.class);
    }

    private String serializeContext(StateMachineContext<TicketState, TicketEvent> context) {
        // 使用Jackson序列化上下文
        try {
            return new ObjectMapper().writeValueAsString(context.getEventHeaders());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("状态机上下文序列化失败", e);
        }
    }

}



