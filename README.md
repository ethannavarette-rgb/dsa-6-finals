# SG-CITE Event Attendance Management System
**College of Information and Communications Technology / Computer Studies**

A comprehensive, crash-proof Java console application demonstrating core **Object-Oriented Programming (OOP)**, **Custom Data Structures (Queue)**, **Sorting (MergeSort)**, and **Searching (Binary Search)** designed for large-scale events of **300 to 500 attendees**.

---

## 📋 Architectural Highlights

1. **Object-Oriented Programming (OOP)**:
   - **Encapsulation**: Strict private fields, public getters/setters, defensive state mutation (`markCheckIn`, `markCheckOut`).
   - **Abstraction**: `CustomQueue` abstracts low-level node linking; `AttendanceManager` coordinates transactions without exposing internal list manipulation.
   - **Separation of Concerns**: Sorting (`MergeSort`), searching (`BinarySearch`), data structure (`CustomQueue`), and business logic (`AttendanceManager`) are modularized into independent classes.

2. **Crash Prevention & Defensive Design**:
   - **No `while(true)` loops**: Every single loop uses an explicit `boolean` flag or bounded condition (`boolean isRunning = true; while (isRunning)`).
   - **Shielded I/O**: Integer parsing (`Integer.parseInt`) is wrapped in try-catch with friendly reprompting; line-based reading prevents trailing-newline skips.
   - **Null & Boundary Guards**: Safe `peek()` and `dequeue()` return `null` instead of throwing `NoSuchElementException`. Binary Search and MergeSort include null checks and empty collection guards.

3. **Algorithm & Data Structure Suitability (300–500 Attendees)**:
   - **Data Structure - Custom Linked Queue (FIFO)**: Simulates the physical check-in scanner line where students queue upon arrival. $O(1)$ enqueue and dequeue.
   - **Sorting - Custom MergeSort**: Guaranteed $O(n \log n)$ time complexity and **stable** sorting. When sorting by program or year, original arrival order is preserved. No $O(n^2)$ worst-case risks.
   - **Searching - Custom Binary Search**: $O(\log n)$ time complexity. For 500 students, binary search locates any attendee in **at most 9 comparisons** ($\lceil \log_2(500) \rceil = 9$).

---

## 🛠️ Transactions Included

| Code | Transaction Name | Description |
| :--- | :--- | :--- |
| **TX-01** | **Enqueue Student Arrival** | Places an arriving student at the rear of the entrance queue (FIFO). Prevents duplicate IDs. |
| **TX-02** | **Process Check-In (Time-In)** | Dequeues student from queue, timestamps check-in, and moves them to the Confirmed Attendance Roster. Supports single or batch processing. |
| **TX-03** | **Process Check-Out (Time-Out)** | Uses Binary Search to locate the attendee and record their departure timestamp. |
| **TX-04** | **Search Attendee Record** | Performs Binary Search by Student ID and prints diagnostic metrics (comparisons made and execution time in ms/ns). |
| **TX-05** | **Sort & View Attendance Roster** | Executes custom MergeSort by Student ID (Ascending), Full Name (A-Z), or Time-In (Chronological) with tabular formatting. |
| **TX-06** | **View Event Summary & Analytics**| Displays metrics on total registered, active inside, checked out, program breakdown (BSIT, BSCS, etc.), and year levels. |
| **TX-07** | **Generate Mock Data (300–500)** | Instantly populates realistic attendee records to easily demonstrate $O(n \log n)$ sorting and $O(\log n)$ searching for university defense. |

---

## 🚀 How to Compile and Run

Open PowerShell or Command Prompt in the project root (`c:\PROJECTS`):

### 1. Compile:
```powershell
javac --release 8 -d bin src/sgcite/*.java
```

### 2. Run Main Program:
```powershell
java -cp bin sgcite.Main
```

### 3. Run Automated System Verification Test:
```powershell
java -cp bin sgcite.SystemVerificationTest
```
