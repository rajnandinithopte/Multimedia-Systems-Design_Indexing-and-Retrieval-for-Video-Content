# Multimedia Systems Design- Indexing and Retrieval for Video Content
## Video Content Search using Hash-based Frame Matching

### Overview
This project implements a **video content search system** that allows users to query an **RGB video frame** and retrieve the corresponding **timestamp and video file** where the frame appears in a stored dataset. 

The system is designed to handle **black frames, repeated frames, noisy video data, short queries, and different FPS rates** efficiently. It also leverages **multithreading** for optimized performance.

---

## 🔷 Tech Stack

**Programming Language:** Java  
**Libraries & Tools:**
- **Google Gson** - For handling JSON data
- **JavaFX Media** - For video playback
- **SHA-256 Hashing** - For unique frame identification  
- **Multithreading (Java Threads)** - For parallel JSON queries  
- **Flask Backend** (JSON Storage) - Preprocessed hashed frames stored as JSON  

---

## 🔷 System Architecture

The system follows a **client-server model**, where:
1. **Client (Java Application)**
   - Accepts **an RGB video file as input**.
   - Extracts **frames and hashes them** using SHA-256.
   - Compares against **preprocessed JSON databases** containing hashed frames.
   - Retrieves the **matching video and timestamp**.

2. **Backend (Flask Server)**
   - Stores precomputed **hashed frames from dataset videos**.
   - Responds to queries by providing **video filenames and timestamps**.

3. **Multithreading**
   - Queries multiple JSON files in **parallel** for faster lookup.
   - Uses **Java Threads** to run concurrent searches.

---

## 🔷 Features:

### 🔶 1. Query Processing
- Accepts an **RGB video file** as input.
- Converts the **first frame** into a **SHA-256 hash**.
- Compares the hash against **hashed frame JSON databases**.
- If a match is found, retrieves:
  - The **corresponding video file**.
  - The **exact timestamp** where the frame appears.

### 🔶 2. Video Playback
- After identifying the **matching video and timestamp**, the system:
  - Loads the **video file using JavaFX MediaPlayer**.
  - Seeks to the **exact timestamp**.
  - Allows **Play, Pause, Reset** controls.

### 🔶 3. Handling Edge Cases
The system is designed to tackle **real-world video search challenges**:

#### **➡ Handling Black Frames**
- Black frames **appear at scene transitions or intros**.
- Solution: **Black frames (RGB `[0,0,0]`) are ignored** until a valid frame is found.

#### **➡ Handling Repeated Frames**
- Some videos contain **static screens (logos, still images)**.
- Solution: **SHA-256 hashes are used to detect and skip identical frames**.

#### **➡ Handling Noisy Video Data**
- Compression artifacts & lighting changes **affect hash matching**.
- Solution: **SHA-256 hashes ensure small differences don’t break frame identification**.

#### **➡ Handling Short Queries**
- Queries with **too few frames** may not contain enough information.
- Solution:
  - Queries with **less than 20 frames are extended**.
  - Matches **multiple short segments** instead of a single frame.

#### **➡ Handling Videos with Different FPS**
- A video might have **different frame rates (24 FPS, 30 FPS, etc.)**.
- Solution:
  - The system **normalizes timestamps** to **30 FPS** before matching.
  - JavaFX **seeks to the exact computed timestamp**.

---

## 🔷 Multithreading Implementation

### 🔶 Why Use Multithreading?
- The system needs to **search multiple JSON files** for matching frame hashes.
- Instead of querying **one-by-one**, we **run searches in parallel** using **Java Threads**.

### 🔶 How It Works
1. The application **loads multiple JSON files** storing hashed frames.
2. It **creates separate threads for each JSON file query**.
3. Threads **execute in parallel**, reducing lookup time.
4. Once **a match is found**, threads **synchronize results**.

---

### 🔶 Video Player Features
The **video playback system** is built using **JavaFX MediaPlayer** and provides the following features:

#### **➡ Play/Pause**
- Users can **start and pause the video at any time**.
- The playback controls allow seamless **pausing and resuming** of the video.

#### **➡ Reset to Query Timestamp**
- The **video resets to the exact timestamp** where the frame match was found.
- Clicking **Reset** jumps back to the detected timestamp and resumes playback.

#### **➡ Auto-Seeking to Matched Frame**
- The player **automatically jumps to the matched frame** when it starts.
- Ensures that the user is immediately directed to the relevant scene in the video.

#### **➡ User-Friendly UI with Buttons**
- A **button interface** allows users to control playback.
- The **buttons (Play, Pause, Reset)** are placed below the video for easy access.

#### **➡ Dynamic Video Selection**
- The player **dynamically loads** the matched video file based on search results.
- Ensures that users are watching the correct video segment without manual intervention.

  ---

## 🔷Performance Considerations
The algorithm is evaluated based on:
- **Correctness of match** (video + frame position)
- **Speed of search & indexing**
- **Audio-video synchronization**
- **User-friendly playback experience**

---

## **🔹 Constraints Considered**
- **All videos are of the same format** (resolution, FPS, etc.).
- **Exact match of query content exists in the database**.
- **Shot boundary detection and digital signatures optimize search speed**.
- **Handles noisy audio and slight video variations**.

---
