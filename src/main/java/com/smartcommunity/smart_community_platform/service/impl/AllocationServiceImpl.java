package com.smartcommunity.smart_community_platform.service.impl;

import com.smartcommunity.smart_community_platform.dao.TicketMapper;
import com.smartcommunity.smart_community_platform.dao.TicketTypeSkillMapper;
import com.smartcommunity.smart_community_platform.dao.UserMapper;
import com.smartcommunity.smart_community_platform.exception.BusinessException;
import com.smartcommunity.smart_community_platform.exception.OptimisticLockException;
import com.smartcommunity.smart_community_platform.model.entity.Ticket;
import com.smartcommunity.smart_community_platform.model.entity.User;
import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import com.smartcommunity.smart_community_platform.service.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * 智能分配服务实现类
 * 使用 @Async 异步处理防止阻塞状态机
 * 通过监听工单完成状态自动减少工人负载
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {
    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;
    private final TicketTypeSkillMapper typeSkillMapper;

    /**
     * 异步自动分配工人
     *
     * @param ticketId 工单ID
     */
    @Async
    @Override
    public void autoAssignWorker(Long ticketId) {
        processAssignment(ticketId);
    }

    /**
     * 处理工单分配核心逻辑
     *
     * @param ticketId 工单ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAssignment(Long ticketId) {
        log.info("自动分配:开始");
        try {
            // 使用悲观锁获取工单
            Ticket ticket = ticketMapper.selectForUpdate(ticketId);
            validateAssignableState(ticket);

            // 查询所需技能并获取可用工人
            String skill = typeSkillMapper.selectSkillByType(ticket.getType());
            List<User> workers = userMapper.selectAvailableWorkers(skill);
            log.info("自动分配:查询到以下工人:\n{}", workers);
            // 选择负载最小的工人
            User worker = workers.stream()
                    .min(Comparator.comparingInt(User::getCurrentLoad))
                    .orElseThrow(() -> new BusinessException("无可用工作人员"));
            log.info("自动分配:分配该工人:\nid: {}  name:{}", worker.getId(), worker.getUsername());
            // 更新工单分配信息
            updateTicketAssignment(ticket, worker);

            // 增加工人负载
            adjustWorkerLoad(worker.getId(), 1, worker.getVersion());
        } catch (Exception e) {
            log.error("工单[{}]自动分配失败: {}", ticketId, e.getMessage());
            throw e;
            // 可在此处添加重试逻辑
        }
    }

    /**
     * 验证工单是否可分配
     */
    private void validateAssignableState(Ticket ticket) {
        if (ticket.getState() != TicketState.CREATED) {
            throw new BusinessException("工单当前状态不可分配");
        }
    }

    /**
     * 更新工单分配信息（带乐观锁）
     */
    private void updateTicketAssignment(Ticket ticket, User worker) {
        ticket.setState(TicketState.AUTO_ASSIGNED);
        ticket.setAssigneeId(worker.getId());
        int count = ticketMapper.updateAssignmentWithLock(ticket);
        if (count == 0) throw new OptimisticLockException("工单并发更新失败");
    }

    /**
     * 调整工人负载（带乐观锁）
     *
     * @param workerId 工人ID
     * @param delta    负载变化量
     * @param version  数据版本号
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void adjustWorkerLoad(Long workerId, int delta, Integer version) {
        int count = userMapper.updateLoadWithLock(workerId, delta, version);
        if (count == 0) throw new OptimisticLockException("工作人员负载更新冲突");
    }
}
