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
```
### Running the System

1. **Start the Load Balancer**:

   Run the Load Balancer with the specified port:

   ```sh
   java LoadBalancer <LoadBalancerPort>
   ```

2. **Start one or more Read Servers**:

   Launch each Read Server with the Load Balancer's port and a unique port for the server:

   ```sh
   java ReadServer <LoadBalancerPort> <ThisServerPort>
   ```
3. **Run the Client**:

   To send read requests and receive responses, start the Client by connecting to the Load Balancer:

   ```sh
   java Client <LoadBalancerHost> <LoadBalancerPort>
   ```
## Usage

After starting the Load Balancer, Read Servers, and the Client, utilize the Client's console to input keys. The system will then retrieve the corresponding value from the distributed storage, efficiently utilizing the LFUCache for optimized data access. This approach ensures quick retrieval times and scalability for read-heavy operations.

## Contributing

Contributions are highly appreciated and vital for the continuous improvement of the project. If you're interested in contributing, please follow these steps:

1. **Fork the Repository**: Click the fork button on the top right of the page to create a copy of the project in your account.
2. **Clone the Repository**: Clone the forked repository to your local machine to make your changes.
3. **Create a New Branch**: Create a branch for your modifications. Naming it relevantly to the changes you intend to make.
4. **Make Your Changes**: Implement your changes, fix a bug, or add new features to the project.
5. **Commit Your Changes**: Commit your changes with a clear and concise commit message.
6. **Push Your Changes**: Push your changes to your forked repository on GitHub.
7. **Submit a Pull Request**: Back on the original repository, submit a pull request from your branch to the main project. Please provide a detailed description of your changes and the benefits they bring.

For any suggestions or if you encounter issues, do not hesitate to open an issue in the GitHub repository. Please provide as much information as possible to facilitate the resolution process.

