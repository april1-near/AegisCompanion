package com.aegis.companion.service;

import com.aegis.companion.model.vo.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aegis.companion.model.dto.AppointmentRequestDTO;
import com.aegis.companion.model.dto.BookingCreateDTO;
import com.aegis.companion.model.dto.ReservationCreateDTO;
import com.aegis.companion.model.dto.TicketCreateDTO;
import com.aegis.companion.model.entity.CommunityRoom;
import com.aegis.companion.model.entity.ParkingReservation;
import com.aegis.companion.model.entity.RoomBooking;
import com.aegis.companion.model.entity.User;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatToolCallingService {
    @Autowired
    private ParkingService parkingService;
    @Autowired
    private ParkingReservationCoreService reservationService;


    //=====================================================车位模块
//    获取空车位
    @Tool(description = "根据区域获取空车位")
    public List<ParkingSpaceVO> getAvailableSpaces(
            @ToolParam(description = "大写区域号，只能在该集合中取值：{A,B,C,D}")
            String zone) {
        return parkingService.getAvailableSpaces(zone);
    }

    //    车位预约
    @Tool(name = "userCreatesAParkingSpaceReservation", description = "创建停车位预约")
    public ParkingReservation createReservation(
            ToolContext toolContext,
            @ToolParam(description = "车位的id，从getAvailableSpaces获取")
            ReservationCreateDTO dto) {
        User user = (User) toolContext.getContext().get("user");
        ParkingReservation reservationId = reservationService.createReservation(dto, user);
        return reservationId;
    }

    //    取消预约
    @Tool(name = "theUserCancelsTheParkingSpaceReservation", description = "取消车位预约,用户取消未过期的、预约")
    public String cancelReservation(
            ToolContext toolContext,
            @ToolParam(description = "车位的id") Long id) {
        User user = (User) toolContext.getContext().get("user");
        reservationService.cancelReservation(id, user);
        return "取消成功";
    }
//    查询预约


    @Tool(name = "userParkingSpaceReservationRecord", description = "获取用户全部车位历史预约（按时间倒序）")
    public List<ParkingReservation> listUserReservations(
            ToolContext toolContext) {
        User user = (User) toolContext.getContext().get("user");
        return reservationService.listUserReservations(user);
    }

    //=====================================================医疗模块
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AppointmentService appointmentService;

//    查询医生

    @Tool(name = "userLooksUpAListOfDoctors", description = "获取所有在岗医生基本信息")
    public List<DoctorVO> listAllDoctors() {
        return doctorService.listAllDoctors();
    }

    //    查询排班
    @Tool(name = "userQueriesTheDoctorSchedule", description = "获取医生未来日期排班表")
    public List<ScheduleVO> getDoctorSchedule(
            @ToolParam(description = "医生的id")
            Long doctorId) {
        return scheduleService.getFutureSchedules(doctorId);
    }
//    预约医疗

    @Tool(name = "userCreatesAMedicalAppointment", description = "提交新的医疗预约申请")
    public AppointmentVO createAppointment(
            ToolContext toolContext,
            @ToolParam(description = "Long doctorId;  LocalDate appointDate; " +
                    " @Pattern(regexp = \"\\\\d{2}:\\\\d{2}-\\\\d{2}:\\\\d{2}\")\n" +
                    "private String timeSlot;")
            AppointmentRequestDTO dto) {
        User user = (User) toolContext.getContext().get("user");
        return appointmentService.createAppointment(dto, user.getId());
    }

//    取消预约

    @Tool(name = "userCancelsTheMedicalAppointment", description = "取消未开始的医疗预约")
    public String cancelAppointment(
            Long appointmentId,
            ToolContext toolContext) {
        User user = (User) toolContext.getContext().get("user");
        appointmentService.cancelAppointment(appointmentId, user.getId());
        return "预约已取消";
    }

    //  查询医疗预约
    @Tool(name = "userMedicalAppointments", description = "查询用户所有的医疗预约记录")
    public List<AppointmentVO> listMyAppointments(
            ToolContext toolContext) {

        User user = (User) toolContext.getContext().get("user");
        return appointmentService.listUserAppointments(user.getId());
    }


    //=====================================================活动模块
    @Autowired
    private RoomBookingService roomBookingService;

    //    查询活动室
    @Tool(name = "userQueriesTheListOfActivityRooms", description = "查询当前可预约的活动室列表")
    public List<CommunityRoom> getAvailableRooms() {
        return roomBookingService.getAvailableRooms();
    }

    //    创建预约
    @Tool(name = "userCreateRoomRequest", description = "用户提交活动室使用申请")
    public BookingRecordVO createBooking(
            @ToolParam(description = "预约申请表" +
                    "    @NotNull(message = \"活动室ID不能为空\")\n" +
                    "    private Long roomId;\n" +
                    "\n" +
                    "    @NotBlank(message = \"用途说明不能为空\")\n" +
                    "    @Size(max = 200, message = \"用途说明长度不能超过200字\")\n" +
                    "    private String purpose;\n" +
                    "\n" +
                    "    @Min(value = 1, message = \"参与人数至少1人\")\n" +
                    "    private Integer participantCount;\n" +
                    "\n" +
                    "    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm\")\n" +
                    "    @Future(message = \"开始时间必须是将来的时间\")\n" +
                    "    private LocalDateTime startTime;\n" +
                    "\n" +
                    "    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm\")\n" +
                    "    @Future(message = \"结束时间必须是将来的时间\")\n" +
                    "    private LocalDateTime endTime;") BookingCreateDTO dto,
            ToolContext toolContext) {
        User user = (User) toolContext.getContext().get("user");
        return roomBookingService.createBooking(dto, user);
    }
//    取消预约

    @Tool(name = "userCancelsTheRoomApplication", description = "用户取消待审批状态的预约申请，仅待审批状态可以取消")
    public String cancelBooking(
            @ToolParam(description = "预约记录通过queryUserRoomApplication查询，活动室预约申请表的id，不是userId" +
                    "private Long id;")
            Long bookingId,
            ToolContext toolContext) {
        User user = (User) toolContext.getContext().get("user");
        roomBookingService.cancelBooking(bookingId, user);
        return "预约已取消";
    }

    //    查询我的活动申请
    @Tool(name = "queryUserRoomApplication", description = "分页查询当前用户的预约历史记录")
    public IPage<BookingRecordVO> queryUserBookings(
            @ToolParam(description = "页码，默认 1") int current,
            @ToolParam(description = "页大小，默认 10") int size,
            ToolContext toolContext) {
        User user = (User) toolContext.getContext().get("user");

        Page<RoomBooking> page = new Page<>(current, size);

        return roomBookingService.queryUserBookings(user, page);
    }


    //=====================================================工单模块
//    创建工单
    @Autowired
    private TicketService ticketService;

    @Tool(name = "userCreatesTicket", description = "用户创建新的服务工单")
    public String createTicket(
            @ToolParam(description = "工单申请表：" +
                    "    @NotBlank(message = \"标题不能为空\")\n" +
                    "    @Size(max = 100, message = \"标题最长100字符\")\n" +
                    "    private String title;\n" +
                    "\n" +
                    "    @NotBlank(message = \"问题描述不能为空\")\n" +
                    "    private String description;\n" +
                    "\n" +
                    "    @NotBlank(message = \"工单类型不能为空\")\n" +
                    "    private String type;" +
                    "工单类型仅能从该集合中取值：{电路维修,管道维修,门窗维修,电器维修,水暖维修}")
            TicketCreateDTO dto,
            ToolContext toolContext) {
        User user = (User) toolContext.getContext().get("user");
        ticketService.createTicket(dto, user.getId());
        return "创建成功";
    }

    //    查询工单
    @Tool(name = "usersQueryTickets", description = "获取当前用户发起的所有工单记录")
    public List<TicketVO> getMyTickets(
            ToolContext toolContext) {
        User user = (User) toolContext.getContext().get("user");

        return ticketService.getUserTickets(user.getId());
    }

}
