# 🖥️ Server Module

Welcome to the **Server** module of the Custom Message Broker! This module provides the server-side implementation, handling incoming messages from clients and sending appropriate responses.
<br><br>

## 🚀 Getting Started

Follow these steps to set up and run your server:

1. **Clone the Repository**

   ```bash
   git clone https://github.com/fadimanakilci/custom-message-broker-server.git
   cd custom-message-broker-server
   ```

2. **Build the Project**

   Make sure you have Maven installed:
   ```bash
   mvn clean install
   ```
   
3. **Run the Server**

   Start the server application with:
    ```bash
   mvn exec:java -Dexec.mainClass="com.sparksign.messagebrokerserver.Main"
   ```
<br><br>

## 📡 Key Features

- **Bidirectional Communication:** Handle incoming messages and send responses to clients.
- **Efficient Message Handling:** Optimized to manage high volumes of messages.
- **Customizable Settings:** Adjust server configurations as needed.
<br><br>

## 💻 Example Usage

Here's a basic Java example for sending and receiving messages:
```java
server.sendMessage("{\"message_type\":1,\"event_type\":1001,\"id\":1,\"device_id\":3250734," +
                           "\"old_value_i32\":\"gnss_status i32\",\"new_value_i32\":\"gnss_status i32\"," +
                           "\"device_date_time\":1701301149,\"server_date_time\":1701301149}");
```
<br><br>

## 📜 Configuration

Adjust your MessageBrokerServer settings in `main`:

- **Host:** The address of the message broker server.
- **Port:** The port number the server is listening on (default: `5678`).

<br><br>

## 🔄 Example Interaction

1. **Run the Server:** Start the server using the command above.
2. **Connect a Client:** Follow the client module `README` to send and receive messages through the broker.




