package com.akoo.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.akoo.api.Emailer;
import com.akoo.data.KeyValues;
import com.akoo.data.StafMessage;
import com.akoo.data.Staff;
import com.akoo.data.User;
import com.akoo.data.VisitorVisitSummary;
import com.akoo.repository.AppointmentRepository;
import com.akoo.repository.MessageRepository;
import com.akoo.repository.StaffRepository;
import com.akoo.repository.TagRepository;
import com.akoo.repository.UserRepository;
import com.akoo.repository.VisitorRepository;

@Controller
public class ReceptionistController {

	@Autowired
	StaffRepository staffRepo;

	@Autowired
	AppointmentRepository appointmentRepo;

	@Autowired
	VisitorRepository visitorRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	UserRepository userRepository;

	@Value("${spring.application.name}")
	String company;

	@Value("${company.name}")
	String companyName;
	
	@Value("${company.value}")
	String companyValue;

	@Autowired
	Emailer emailService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	MessageRepository messageRepository;

	@Autowired
	@Value("${app.host.ip}")
	private String serverHost;

	@RequestMapping("/admin/dashboard")
	public String getPage(Model model, Principal principal, HttpSession session) {
		System.err.println(companyName + "---------");
		session.setAttribute("companyName", companyName);
		User user = userRepository.getUser(principal.getName());
		int[] summary = visitorRepository.visitorSummaryToday();
		int[] systemSummary = visitorRepository.visitorDataSummary();
		if (user.getRole().equalsIgnoreCase("Administrator")) {
			List<Integer> setdata = userRepository.getReceptionistCount();
			model.addAttribute("count", summary[0]);
			model.addAttribute("receptionCount", setdata.get(0));
			model.addAttribute("staffCount", setdata.get(1) == 0 ? 1 : setdata.get(1));
			model.addAttribute("active", summary[1]);
			model.addAttribute("deptCount", setdata.get(2));
			model.addAttribute("staffVisitorCount", staffRepo.staffVistorsCount());
			model.addAttribute("distinctVisitors", systemSummary[0]);
			model.addAttribute("appointmentAcceptance", systemSummary[1]);
			model.addAttribute("visits", systemSummary[3]);
			model.addAttribute("staff", systemSummary[2]);
			model.addAttribute("departments", systemSummary[6]);
			model.addAttribute("visitAcceptance", systemSummary[4]);
			model.addAttribute("appointments", systemSummary[7]);
			model.addAttribute("yearVisit", systemSummary[8]);
			model.addAttribute("totalStaff", systemSummary[9]);

		} else {
			model.addAttribute("active", summary[1]);
			model.addAttribute("apps", summary[2]);
			model.addAttribute("tags", tagRepository.getTagsRemaining());
			model.addAttribute("acceptVisits", visitorRepository.getVisitsAcceptButNoTags());
			model.addAttribute("count", summary[0]);

		}

		model.addAttribute("company", company);
		model.addAttribute("title", "dashboard");
		return "admin/dash";
	}

	@RequestMapping("/admin/unanswered/appointments")
	public String unAnswered(Model model) {
		model.addAttribute("appointments", appointmentRepo.getAllUnAnsweredAppointments());
		model.addAttribute("title", "appointments");
		return "admin/appointments";

	}

	@RequestMapping("/admin/appointments/repsonse")
	public String repsonseAppoinments(Model model) {
		model.addAttribute("appointments", appointmentRepo.getAnsweredAppointments());
		model.addAttribute("title", "appointments");
		return "admin/appointments";
	}

	@RequestMapping("/admin/visits")
	public String getVisits(Model model) {
		model.addAttribute("title", "visits");
		model.addAttribute("visits", visitorRepository.getVisitsToday());
		return "admin/visits";
	}
	
	@RequestMapping("/admin/visitors/report")
	public String getVisitors(Model model) {
		model.addAttribute("title", "visits");
		model.addAttribute("visitors", visitorRepository.getVisitors());
		return "admin/visitor";
	}

	@RequestMapping("/admin/messages")
	public String getMessages(Model model) {
		model.addAttribute("title", "messages");
		model.addAttribute("msgs", messageRepository.getReceptionistMessages());
		return "admin/messages";
	}

	@RequestMapping("/register")
	public String registerAdmin(Model model) {
		model.addAttribute("company", companyValue);
		try {
			String t = redisTemplate.opsForValue().get(companyName+":init");
			if (t == null) {
				return "admin/register";
			} else {

				return "admin/auth_login";
			}
		} catch (Exception e) {
			return "admin/register";
		}

	}

	@PostMapping("/admin/get/visit/")
	public String getVisits(@RequestParam("from") String from, @RequestParam("to") String to, Model model) {
		System.out.println(from);
		model.addAttribute("from", from);
		model.addAttribute("to", to);
		model.addAttribute("visits", visitorRepository.getVisitsByDate(from, to));
		return "admin/datevisits";
	}

	@ResponseBody
	@PostMapping("/admin/add/new/staff")
	public int addNewStaff(@RequestParam("lastname") String lastName, @RequestParam("firstname") String firstName,
			@RequestParam("email") String email, @RequestParam("dept") int department,
			@RequestParam("contact") String telephone, @RequestParam("position") String position) {
		System.out.println(email);

		return staffRepo.saveStaff(email, telephone, position, department, firstName, lastName);
	}

	@RequestMapping("/get/server/ip")
	@ResponseBody
	public String ip() {
		
		/*
		 * try { DatagramSocket socketIP = new DatagramSocket();
		 * socketIP.connect(InetAddress.getByName("8.8.8.8"), 10002); String ips =
		 * socketIP.getInetAddress().getHostAddress(); socketIP.close(); return ips; }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); return ""; }
		 */
		 return "vmi582247.contaboserver.net";

	}

	@RequestMapping("/get/visitors/by/departments")
	@ResponseBody
	public List<KeyValues> getVisitorsByDepartments() {
		return visitorRepository.visitsByDepartments();
	}

	@RequestMapping("/get/reception/messages")
	@ResponseBody
	public List<StafMessage> getMessagesReception() {
		return messageRepository.getReceptionistMessages(5);
	}

	@RequestMapping("/get/visitors/by/mission")
	@ResponseBody
	public List<KeyValues> getVisitsByMission() {
		return visitorRepository.visionBymission();
	}

	@RequestMapping("/get/visits/in/year")
	@ResponseBody
	public List<KeyValues> getVisitsInYear() {
		return visitorRepository.monthlyVisits();
	}

	@RequestMapping("/get/visits/by/year")
	@ResponseBody
	public List<KeyValues> getYearlyVisits() {
		return visitorRepository.yearlyVisits();
	}

	@ResponseBody
	@PostMapping("/admin/add/tag")
	public long addTag(@RequestParam("tag") int tag) {

		return tagRepository.saveTag(tag);
	}

	@ResponseBody
	@PostMapping("/admin/delete/dept")
	public long deleteDept(@RequestParam("id") int id) {

		return staffRepo.deleteDept(id);
	}

	@ResponseBody
	@PostMapping("/admin/delete/staff")
	public long deleteStaff(@RequestParam("id") String id) {

		return staffRepo.deleteStaff(id);
	}

	@ResponseBody
	@PostMapping("/admin/suspend/staff")
	public long suspendStaff(@RequestParam("id") String id) {

		return staffRepo.suspendStaff(id);
	}

	@ResponseBody
	@PostMapping("/admin/activate/staff")
	public long activeateSaff(@RequestParam("id") String id) {

		return staffRepo.activateStaff(id);
	}

	@ResponseBody
	@PostMapping("/admin/change/password")
	public void changePassword(@RequestParam("id") int id, @RequestParam("password") String password) {

		userRepository.changePassword(id, password);
	}

	@ResponseBody
	@PostMapping("/admin/change/user/password")
	public void changePassword(Principal principal, @RequestParam("password") String password) {

		userRepository.changePassword(principal.getName(), password);
	}

	@ResponseBody
	@PostMapping("/admin/message/send")
	public void sendMessage(@RequestParam("id") String id, @RequestParam("message") String message) {
		messageRepository.sendNotfiyStaff(id, message);
	}

	@ResponseBody
	@GetMapping("/admin/get/waiting/visitors")
	public List<VisitorVisitSummary> getWatining() {

		return visitorRepository.getWaitingVisitsToday();
	}

	@ResponseBody
	@GetMapping("/admin/reception/signout")
	public void visitorSignout(@RequestParam("id") String id) {

		visitorRepository.visitorWebAppSignOut(id);
	}

	@ResponseBody
	@GetMapping("/admin/get/visit/type/graph")
	public List<KeyValues> addTag() {

		return visitorRepository.getSummaryData();
	}

	@ResponseBody
	@GetMapping("/admin/get/visit/type/summary")
	public List<KeyValues> getVisitTypeSummary() {

		return visitorRepository.visitsByMission();
	}

	@ResponseBody
	@PostMapping("/admin/add/department")
	public void addTag(@RequestParam("name") String dept) {

		staffRepo.saveDept(dept);
	}

	@ResponseBody
	@PostMapping("/admin/assign/tag")
	public void renameDepartment(@RequestParam("tag") String tag, @RequestParam("id") long id) {

		visitorRepository.giveTags(id, tag);
	}

	@ResponseBody
	@GetMapping("/admin/found/{tag}")
	public long foundTag(@PathVariable("tag") int tag) {

		return tagRepository.found(tag);
	}

	@ResponseBody
	@PostMapping("/admin/rename/department")
	public void assignTag(@RequestParam("name") String dept, @RequestParam("id") long id) {

		staffRepo.renameDepartment(dept, id);
	}

	@ResponseBody
	@GetMapping("/admin/remove/tag/{tag}")
	public void removeTag(@PathVariable("tag") int tag) {

		tagRepository.delete(tag);
	}

	@RequestMapping("/admin/calendar")
	public String getCalendar(Model model) {
		model.addAttribute("title", "reports");
		return "admin/daysreport";
	}

	@RequestMapping("/admin/users")
	public String addUser(Model model) {

		model.addAttribute("users", userRepository.getUsers());
		return "admin/newuser";
	}

	@RequestMapping("/api/ping")
	public String email(Model model) {
		return "admin/ping";
	}

	@RequestMapping("/admin/add/staff")
	public String addStaffView(Model model) {
		model.addAttribute("title", "staff");
		model.addAttribute("dept", staffRepo.getAllDepartment());
		return "admin/newstaff";
	}

	@ResponseBody
	@PostMapping("/admin/delete/user")
	public void deleteUser(@RequestParam("id") int id) {

		userRepository.deletetUser(id);
	}

	@ResponseBody
	@PostMapping("/admin/suspend/user")
	public void suspendUser(@RequestParam("id") int id) {

		userRepository.suspendUser(id);
	}

	@ResponseBody
	@PostMapping("/admin/activate/user")
	public void activateUser(@RequestParam("id") int id) {

		userRepository.activate(id);
	}

	@ResponseBody
	@GetMapping("/api/emailers")
	public void emailer() {
		// emailService.sendEmail();
		emailService.prepareAndSend(appointmentRepo.getAppointmentById("1"));

	}

	@PostMapping(value = "/add/superuser")
	public String addUser(@RequestParam(value = "name") String username, @RequestParam(value = "pass") String password,
			@RequestParam(value = "email") String email) {
		User user = new User();
		user.setPassword(password);
		user.setUsername(username);
		user.setRole("Administrator");
		user.setEmail(email);
		user.setStatus("1");
		userRepository.insertUser(user);
		redisTemplate.opsForValue().set("init", "1");
		return "admin/auth_login";

	}

	@RequestMapping("/admin/departments")
	public String departments(Model model) {
		model.addAttribute("depts", staffRepo.getAllDepartment());
		return "admin/departments";
	}

	@RequestMapping("/admin/staff/report")
	public String getVisitsstaff(Model model) {
		model.addAttribute("title", "reports");
		model.addAttribute("staff", staffRepo.getAllStaff());

		return "admin/staffreport";
	}

	@RequestMapping("/admin/tags/report")
	public String getTags(Model model) {
		model.addAttribute("tags", tagRepository.getTags());
		model.addAttribute("title", "tags");

		return "admin/tags";
	}

	@RequestMapping("/admin/add/staff/batch")
	public String addStaffDataBatch(Model model) {
		model.addAttribute("title", "staff");

		return "admin/staffbatch";
	}

	@RequestMapping({ "/login", "/" })
	public String geters(Model model) {
		model.addAttribute("company", companyValue);

		return "admin/auth_login";
	}

	@RequestMapping("/admin/staff/{id}/report")
	public String getVisitsstaff(Model model, @PathVariable("id") String id) {
		model.addAttribute("staff", staffRepo.getStaff((id)));
		model.addAttribute("visits", visitorRepository.getStaffVisitorsByTelephone(id));
		model.addAttribute("title", "reports");
		return "admin/staffvisitors";
	}

	@RequestMapping("/admin/staff")
	public String getStaff(Model model) {
		model.addAttribute("staff", staffRepo.getAllStaff());
		model.addAttribute("title", "staff");
		return "admin/staff";
	}

	@RequestMapping("/admin/new/visit")
	public String newVisit(Model model) {
		model.addAttribute("staff", staffRepo.getAllStaff());
		model.addAttribute("title", "visits");
		return "admin/newvisit";
	}

	@PostMapping("/admin/add/batch/data")
	public ModelAndView addBatchData(@RequestParam("file") MultipartFile file,Model model) {
		List<Staff> staffs =staffRepo.extractStaff(file);
		model.addAttribute("staff",staffs);
		return new ModelAndView("redirect:/admin/staff","sdfsd","sdfsd");
	}

}
