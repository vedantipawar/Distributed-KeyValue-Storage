# Distributed Key-Value Storage with LFUCache

This project implements a distributed key-value storage system optimized for read-heavy workloads, utilizing an efficient Least Frequently Used (LFU) cache mechanism to enhance data retrieval speeds and system scalability. The system is composed of a Load Balancer, Read Servers, and a Client module, leveraging advanced data structures for optimized eviction policies.

## Components

- **Load Balancer**: Distributes incoming read requests among multiple Read Servers using a round-robin approach, handles dynamic server registration, and integrates an LFUCache to improve latency and throughput.
- **Read Server**: Manages a subset of the key-value data and processes read requests from the Load Balancer. Supports dynamic scaling.
- **Client**: Interfaces with the system, allowing users to send read requests and receive responses.
- **LFUCache**: A custom cache implementation on the Load Balancer for storing and efficiently retrieving key-value pairs based on access frequency.

## Setup and Running

### Prerequisites

- Ensure Java Development Kit (JDK) is installed on your machine.

### Compilation

First, compile all Java files. Example:

```sh
javac LoadBalancer.java ReadServer.java Client.java LFUCache.java
