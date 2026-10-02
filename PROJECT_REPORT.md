# MICRO-PROJECT REPORT
## Subject: Object-Oriented Programming (Java)
### Course: Bachelor of Technology (B.Tech) in Computer Science & Engineering

---

# PROJECT TITLE: SILENT SOS
### *A Covert Emergency Distress Dispatch System with Decoy User Interface*

---

### **Submitted By:**
- **Student Name:** [Your Name]
- **Roll Number / PRN:** [Your Roll Number]
- **Branch / Semester:** Computer Science & Engineering / [Your Semester]
- **Academic Year:** 2025 – 2026

### **Under the Guidance of:**
- **Project Guide:** [Professor / Guide Name]
- **Department:** Department of Computer Science & Engineering
- **Institution:** [Your College / Institute Name]

---

## 📜 CERTIFICATE OF AUTHENTICITY

*This is to certify that the micro-project entitled **"SILENT SOS: Covert Emergency Distress Dispatch System"** has been successfully carried out and submitted by **[Your Name]** (Roll No: **[Your Roll Number]**) in partial fulfillment of the requirements for the degree of **Bachelor of Technology in Computer Science & Engineering** in the subject of **Object-Oriented Programming** during the academic year 2025-2026.*

<br><br>
_________________________ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; _________________________
**Project Guide** &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **Head of Department (CSE)**

---

## 📝 ABSTRACT

In situations involving severe personal danger—such as assaults, stalking, domestic abuse, or armed intrusions—a victim often cannot safely access a telephone, dial emergency hotlines, or open conspicuous distress applications without alerting the aggressor. 

**Silent SOS** is an Object-Oriented Java software system engineered to solve this dilemma. The application conceals its primary function behind a fully operational **Decoy Calculator Graphical User Interface (GUI)**. When an emergency arises, the user inputs covert key combinations or arithmetic equations (e.g., `911=`, `999=`, or `Ctrl + Shift + S`). 

Without alerting bystanders or displaying visual alarms, the system dispatches an immutable **SOS Distress Packet** containing simulated GPS coordinates, timestamps, and network telemetry to registered emergency contacts via multi-threaded SMS, Email, and Local Audit channels.

This micro-project exemplifies the core concepts of Object-Oriented Programming (OOP) including **Encapsulation**, **Inheritance**, **Polymorphism**, and **Abstraction**, while incorporating production-level software design patterns such as the **Singleton**, **Factory**, and **Observer** patterns.

**Keywords:** *Object-Oriented Programming, Java Swing, Concurrency, Covert Security, Design Patterns, Emergency Dispatch.*

---

## 📑 TABLE OF CONTENTS

1. **Chapter 1: Introduction**
   - 1.1 Problem Definition
   - 1.2 Motivation
   - 1.3 Project Objectives
   - 1.4 Scope of the System
2. **Chapter 2: Literature Survey & Comparative Analysis**
   - 2.1 Study of Existing Systems
   - 2.2 Shortcomings of Conventional Solutions
   - 2.3 Proposed Advantages
3. **Chapter 3: Software Requirements Specification (SRS)**
   - 3.1 Hardware Requirements
   - 3.2 Software Requirements
   - 3.3 Functional Requirements
   - 3.4 Non-Functional Requirements
4. **Chapter 4: System Architecture & UML Design**
   - 4.1 High-Level Architecture
   - 4.2 UML Class Diagram
   - 4.3 UML Sequence Diagram
5. **Chapter 5: Object-Oriented Implementation Details**
   - 5.1 Pillars of OOP in Silent SOS
   - 5.2 Software Design Patterns
   - 5.3 Concurrency and Asynchronous Processing
6. **Chapter 6: Testing & Test Cases**
   - 6.1 Testing Strategy
   - 6.2 Test Case Matrix
7. **Chapter 7: Results & Output Discussion**
   - 7.1 Runtime Execution Flow
   - 7.2 Sample Audit Trail
8. **Chapter 8: Conclusion & Future Scope**
   - 8.1 Summary
   - 8.2 Future Enhancements
9. **Appendix: Viva Voce Questions & Model Answers**

---

## CHAPTER 1: INTRODUCTION

### 1.1 Problem Definition
Traditional emergency safety tools rely on overt interaction: dialing 112/911, opening dedicated emergency apps with large red panic buttons, or sending loud alert notifications. In situations where an assailant is observing the victim, overt actions can provoke immediate violence.

### 1.2 Motivation
Safety technologies must prioritize **victim discretion**. By embedding an emergency distress trigger inside a mundane, ubiquitous desktop utility—a calculator—the user can trigger a multi-channel emergency broadcast in plain sight.

### 1.3 Project Objectives
- To develop a modular, object-oriented Java application that combines a covert decoy user interface with a background dispatch engine.
- To rigorously implement and showcase the 4 pillars of OOP (Abstraction, Encapsulation, Inheritance, Polymorphism).
- To utilize design patterns (Singleton, Factory, Observer) to structure enterprise-ready, maintainable code.
- To execute alerts asynchronously using thread pools so the decoy interface remains responsive and deceptive.

### 1.4 Scope of the System
The current micro-project focuses on a desktop Java environment featuring:
- Standard arithmetic computation capability.
- Covert pattern recognition engine (`911=`, `999=`, `Ctrl+Shift+S`).
- Mock SMS gateway transmission, formatted SMTP email transmission, and append-only local file logging.

---

## CHAPTER 2: LITERATURE SURVEY & COMPARATIVE ANALYSIS

| Parameter | Traditional Panic Apps | Dialing Emergency Hotlines | Silent SOS (Proposed) |
| :--- | :--- | :--- | :--- |
| **Visibility** | High (Conspicuous UI & Red Icons) | Very High (Audible Voice Call) | **Zero (Disguised as Calculator)** |
| **Speed to Trigger** | Medium (Unlock phone, find app) | Slow (Dialing, ringing, speaking) | **Instant (3 keypresses / 1 hotkey)** |
| **Attacker Detection** | Probable escalation | High likelihood of physical intervention | **Undetected by bystander** |
| **Multi-Channel Dispatch** | Typically SMS only | Voice only | **SMS + Email + Audit Log simultaneously** |
| **System Overhead** | High | Low | **Extremely Low (Lightweight Java VM)** |

---

## CHAPTER 3: SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

### 3.1 Hardware Requirements
- **Processor**: Intel Core i3 / AMD Ryzen 3 or higher.
- **RAM**: Minimum 2 GB (4 GB recommended).
- **Disk Storage**: 50 MB free hard drive space.
- **Input Devices**: Standard Keyboard and Mouse.

### 3.2 Software Requirements
- **Operating System**: Windows 10/11, macOS, or Linux.
- **Runtime Environment**: Java Runtime Environment (JRE) / Java Development Kit (JDK) 17 or higher.
- **Development Tools**: VS Code, IntelliJ IDEA, Eclipse, or Command-line `javac`.

### 3.3 Functional Requirements
1. **Arithmetic Computation**: Perform basic operations (+, -, *, /) with decimal precision.
2. **Covert Trigger Detection**: Detect predefined code sequences without throwing exceptions or visual cues.
3. **Distress Data Packaging**: Gather timestamp, latitude, longitude, and trigger source into an immutable packet.
4. **Emergency Contact Management**: Store contacts with phone numbers, emails, and priority levels.
5. **Multi-Channel Alerting**: Broadcast the packet to all contacts through SMS, Email, and Audit log.

### 3.4 Non-Functional Requirements
- **Stealth & Discretion**: Latency between trigger and UI reset must be < 50ms; no audible beeps or popups.
- **Reliability & Concurrency**: Network dispatch must occur on background threads to prevent UI freezing.
- **Data Integrity**: Audit records must be thread-safe and append-only.

---

## CHAPTER 4: SYSTEM ARCHITECTURE & UML DESIGN

### 4.1 UML Sequence Diagram
```text
User            DecoyCalculatorUI       SOSManager           AlertNotifiers (SMS/Email/Log)
 |                     |                    |                              |
 |-- Types "911=" ---->|                    |                              |
 |                     |-- triggerSOS() --->|                              |
 |                     |<-- (Return fast) --|                              |
 |<-- Display resets --|                    |                              |
 |    to "0"           |                    |-- Dispatch via ThreadPool -->|
 |                     |                    |   (SMSNotifier)              |====> Network SMS
 |                     |                    |   (EmailNotifier)            |====> SMTP Email
 |                     |                    |   (AuditLogNotifier)         |====> sos_audit.log
```

---

## CHAPTER 5: OBJECT-ORIENTED IMPLEMENTATION DETAILS

### 5.1 The Four Pillars of OOP in the Project

#### 1. Encapsulation
- Demonstrated in `Contact.java` and `SOSPacket.java`.
- Fields (`name`, `phone`, `email`, `priority`) are declared `private`.
- Modifiers include validation logic (e.g., throwing `IllegalArgumentException` on empty names or malformed emails) to preserve internal object validity.

#### 2. Abstraction
- Demonstrated in the `AlertNotifier` interface.
- Exposes `boolean sendAlert(SOSPacket packet, Contact contact)`.
- The caller (`SOSManager`) does not need to know whether the message is sent over GSM radio, SMTP sockets, or local file descriptors.

#### 3. Inheritance
- Demonstrated in `AbstractNotifier.java` which implements `AlertNotifier`.
- Concrete subclasses `SMSNotifier`, `EmailNotifier`, and `AuditLogNotifier` extend `AbstractNotifier`.
- Inherits common pre-dispatch logging and telemetry reporting while overriding the specific `doDispatch()` logic.

#### 4. Polymorphism
- Demonstrated through runtime dynamic binding.
- `SOSManager` maintains a `List<AlertNotifier>`.
- During execution, the loop calls `notifier.sendAlert()`. Java invokes the appropriate overridden method dynamically at runtime based on the actual object type.

### 5.2 Software Design Patterns
- **Singleton Pattern**: Implemented in `SOSManager`. Ensures centralized emergency state and thread pool allocation.
- **Factory Pattern**: Implemented in `NotifierFactory` to instantiate notifiers through enum types (`SMS`, `EMAIL`, `AUDIT_LOG`).
- **Observer Pattern**: `SOSObserver` interface enables decoupled subscribers to react whenever a trigger is detected.

---

## CHAPTER 6: TESTING & TEST CASES

| Test ID | Test Scenario | Input / Action | Expected Result | Status |
| :---: | :--- | :--- | :--- | :---: |
| **TC-01** | Standard Calculation | Enter `15 + 27 =` | Display shows `42`; no SOS triggered. | **PASS** |
| **TC-02** | Covert Code Trigger | Enter `911=` | Display resets to `0`; background SOS dispatched. | **PASS** |
| **TC-03** | Hotkey Trigger | Press `Ctrl + Shift + S` | Calculator remains unchanged; SOS dispatches instantly. | **PASS** |
| **TC-04** | Invalid Contact Validation | Add Contact with invalid email | Throws `IllegalArgumentException`; prevents corrupted state. | **PASS** |
| **TC-05** | Audit Log Persistence | Fire SOS trigger | Log file `sos_audit.log` appends record with valid timestamp. | **PASS** |
| **TC-06** | UI Responsiveness Test | Trigger SOS under slow network | UI does not freeze; thread pool isolates dispatch latency. | **PASS** |

---

## CHAPTER 7: RESULTS & OUTPUT DISCUSSION

### Sample Console Output During Covert Trigger:
```text
=================================================
  🚨 [SILENT SOS ENGAGED] DISPATCHING SIGNALS 🚨 
=================================================
[SMS-GATEWAY] Preparing alert for Emergency Services (Priority 1)...
  -> Sending SMS to Emergency Services (+919876543210)...
     Text: "EMERGENCY! Coordinates: 12.9716° N, 77.5946° E. Help needed!"
[SMS-GATEWAY] SUCCESS: Dispatched to Emergency Services.

[EMAIL-SMTP] Preparing alert for Family / Guardian (Priority 1)...
  -> Sending Encrypted Email to Family / Guardian (family.emergency@gmail.com)...
     Subject: [CRITICAL] SILENT SOS DISTRESS SIGNAL
[EMAIL-SMTP] SUCCESS: Dispatched to Family / Guardian.

[LOCAL-AUDIT-LOG] Preparing alert for Campus Security (Priority 2)...
  -> Event written to sos_audit.log successfully.
[LOCAL-AUDIT-LOG] SUCCESS: Dispatched to Campus Security.
=================================================
```

---

## CHAPTER 8: CONCLUSION & FUTURE SCOPE

### 8.1 Conclusion
The **Silent SOS** micro-project successfully demonstrates how Object-Oriented Programming concepts can be synthesized into a practical, life-saving software application. By combining strict OOP encapsulation, polymorphism, and design patterns with multithreaded background processing, the application guarantees high discretion and zero lag.

### 8.2 Future Scope
1. **Live Twilio / AWS SNS API**: Connect mock SMS handlers to actual live SMS gateways.
2. **Audio Streaming**: Buffer 15 seconds of ambient microphone audio and attach it to the distress email.
3. **Mobile Android Version**: Port core models to Android with volume button or shake-to-trigger detection.

---

## 🎓 APPENDIX: VIVA VOCE QUESTIONS & MODEL ANSWERS

**Q1: Why did you choose Java for this project?**  
*Answer:* Java provides strong object-oriented principles, platform independence via the JVM, robust built-in multithreading (`ExecutorService`), and standard GUI capabilities (`Swing/AWT`) without external dependencies.

**Q2: How does the Decoy Calculator maintain secrecy?**  
*Answer:* The calculator functions normally for all mathematical operations. When the trigger sequence (`911=`) is entered, the UI swallows the command, resets the text to `0`, and dispatches the alert asynchronously on a background worker thread.

**Q3: Where is Polymorphism used in your code?**  
*Answer:* The `AlertNotifier` interface has multiple child implementations (`SMSNotifier`, `EmailNotifier`, `AuditLogNotifier`). The `SOSManager` iterates through `List<AlertNotifier>` calling `sendAlert()`, and the JVM dynamically executes the corresponding child class implementation at runtime.

**Q4: Why did you use the Singleton pattern for `SOSManager`?**  
*Answer:* Having multiple instances of the emergency manager could cause race conditions, duplicate alert transmissions, and multiple thread pools. The Singleton pattern guarantees exactly one point of coordination.

**Q5: Why is Multithreading necessary in this application?**  
*Answer:* In Java Swing, GUI events run on the Event Dispatch Thread (EDT). If network requests (SMS, Email) take several seconds, the calculator would freeze, alerting an attacker that something unusual happened. Running alerts in a background `ExecutorService` keeps the GUI completely smooth.
