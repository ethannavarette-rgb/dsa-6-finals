# SG-CITE Event Attendance Management System
**College of Information and Communications Technology / Computer Studies**

A robust, crash-proof Java console application demonstrating core **Object-Oriented Programming (OOP)**, **Custom Data Structures (FIFO Queue & LIFO Stack)**, **Sorting (Divide-and-Conquer MergeSort)**, and **Searching (Binary Search)** engineered for departmental assemblies and academic events across **1,500 students** and **15 block sections**.

---

## 📋 Architectural Highlights

1. **Object-Oriented Programming (OOP)**:
   - **Encapsulation**: Strict private fields, defensive accessors, and state mutators across `Student`, `Officer`, and `Event` models.
   - **5W1H Event Structure**: Events encapsulate `What`, `Who`, `When`, `Where`, `Why`, and `How` fields with structured console rendering.
   - **Separation of Concerns**: Portals (`OfficerPortal`, `StudentPortal`), DSA engines (`MergeSort`, `BinarySearch`), dynamic collections (`CheckInQueue`, `OfficerActionStack`), and in-memory persistence (`DataStore`) are modularized into dedicated packages.

2. **Crash Prevention & Defensive Design**:
   - **No Unbounded Loops**: All iterations use explicit boolean control flags or bounded counters.
   - **Shielded I/O**: Safe scanner parsing, line trimming, and friendly error reprompts (`Exit? YES/NO`).
   - **Null & Boundary Guards**: Defensive comparator checks, empty collection fallbacks, and boundary assertions across all algorithms.

3. **Algorithm & Data Structure Suitability (1,500 Students)**:
   - **Custom FIFO Queue (`CheckInQueue`)**: Accurately simulates the physical entrance scanner line where students arrive in real time. $\mathcal{O}(1)$ arrival enqueueing protected by an $\mathcal{O}(1)$ duplicate-detection `HashSet`.
   - **Custom LIFO Stack (`OfficerActionStack`)**: Maintains chronological administrative audit logs and rollback history for officer operations. $\mathcal{O}(1)$ push and pop operations.
   - **Divide-and-Conquer Merge Sort (`MergeSort`)**: Guaranteed $\mathcal{O}(n \log n)$ performance across best, average, and worst cases with strict stability (`<= 0`) preserving FIFO arrival order for identical keys.
   - **Binary Search Engine (`BinarySearch`)**: High-performance $\mathcal{O}(\log n)$ searching. Locates any student out of 1,500 in **at most 11 comparisons** ($\lceil \log_2(1,500) \rceil = 11$).

---

## ⚡ DSA Time & Space Complexity Justification

| Component | Operation / Transaction | Time Complexity | Space Complexity | Justification & Architectural Suitability |
| :--- | :--- | :--- | :--- | :--- |
| **`CheckInQueue`** | Enqueue Student Arrival (`enqueueCheckIn`) | $\mathcal{O}(1)$ | $\mathcal{O}(n)$ | Constant-time insertion with an internal `HashSet` checking ID uniqueness in $\mathcal{O}(1)$ to prevent duplicate scanner scans. |
| **`CheckInQueue`** | Flush Queue to Attendance (`flushQueueToAttendance`) | $\mathcal{O}(k)$ | $\mathcal{O}(1)$ auxiliary | Linearly drains $k$ queued students, marking them checked in and updating `sectionAttendanceMap` in a single pass. |
| **`OfficerActionStack`** | Audit Action Logging (`pushAction`, `popLastAction`) | $\mathcal{O}(1)$ | $\mathcal{O}(m)$ | LIFO behavior ensures administrative updates and login events are immediately accessible for security inspection. |
| **`MergeSort`** | Student & Officer Pre-indexing (`sortStudentsById`, `sortOfficersById`) | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n)$ auxiliary | Guaranteed $\mathcal{O}(n \log n)$ stable sort avoiding worst-case quadratic degradation ($\mathcal{O}(n^2)$) of quicksort on edge inputs. |
| **`MergeSort`** | Section Attendance Breakdown (`bySectionAndName`) | $\mathcal{O}(n \log n)$ | $\mathcal{O}(n)$ auxiliary | Stably clusters attendees by academic block (`BSIT-1101` .. `BSIT-4103`) then sorts alphabetically by surname. |
| **`BinarySearch`** | Student Login & Record Lookup (`findStudentById`) | $\mathcal{O}(\log n)$ | $\mathcal{O}(1)$ | Resolves student records across 1,500 items in $\le 11$ comparisons, eliminating linear scan latency. |
| **`BinarySearch`** | Officer Whitelist Authentication (`findOfficerById`) | $\mathcal{O}(\log m)$ | $\mathcal{O}(1)$ | Immediate whitelist credentials lookup across registered officers in $\le 4$ comparisons. |
| **`DataStore`** | Section Map Tallying (`sectionAttendanceMap`) | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ (15 blocks) | Constant-time block tallying across all 15 predefined academic year sections. |

---

## 🛠️ System Transactions & Workflows

| Code | Transaction | Role | Description |
| :--- | :--- | :--- | :--- |
| **TX-01** | **Role Selection Menu** | Public | Main console navigation loop routing to SG CITE Officer Portal, Student Portal, or Shutdown. |
| **TX-02** | **Student Portal Auth** | Student | Validates Student ID via Binary Search; reprompts on failure or exits to main menu. |
| **TX-03** | **Event Check-In** | Student | Queues student arrival in FIFO entrance queue; rejects duplicates. |
| **TX-04** | **Submit Requirements** | Student | Validates prior check-in and records physical/digital requirement submission. |
| **TX-05** | **Officer Whitelist Auth** | Officer | Validates credentials against pre-sorted `DataStore.officers` via Binary Search. |
| **TX-06** | **Manage Events (5W1H)** | Officer | Views active events and registers new events capturing What, Who, When, Where, Why, and How. |
| **TX-07** | **Section Attendance Report**| Officer | Flushes queue to section map, sorts attendees via MergeSort, and prints formatted breakdown. |
| **TX-08** | **Member Roster Management**| Officer | Adds/removes officers and immediately re-indexes whitelist via MergeSort. |
| **TX-09** | **Audit Trail Logging** | Officer | Tracks administrative actions and modifications in OfficerActionStack. |

---

## 🚀 How to Compile and Run

### Prerequisites
- **Java SE Development Kit (JDK 8 or higher)**

### 1. Compile All Source Files:
```bash
# Unix / macOS:
find src/main/java -name "*.java" > sources.txt
javac -d bin @sources.txt

# Windows (Command Prompt / PowerShell):
javac -d bin src/main/java/org/sgcite/attendance/*.java src/main/java/org/sgcite/attendance/*/*.java
```

### 2. Run Main Program:
```bash
java -cp bin org.sgcite.attendance.Main
```

### 3. Run Automated Verification Tests:
```bash
# Compile all source and test classes:
find src -name "*.java" > all_sources.txt
javac -d bin @all_sources.txt

# Run Officer Portal Verification Test (Issues #7, #8, #9):
java -cp bin org.sgcite.attendance.OfficerPortalVerificationTest

# Run Main Console Loop Integration & QA Test (Issue #10):
java -cp bin org.sgcite.attendance.MainVerificationTest
```
