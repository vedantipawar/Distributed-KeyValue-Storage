# Distributed Key-Value Storage with LFUCache

This project implements a distributed key-value storage system which enables clients to perform read and write operations efficiently. The system comprises a load balancer and multiple read servers. When a client initiates a write operation, the load balancer broadcasts the value to all read servers in the network, ensuring consistency across the system. Additionally, new servers can be seamlessly added to the network, with the load balancer requesting recent data snapshots from existing servers to synchronize the new server's state.

## Components

- **Load Balancer**: Distributes incoming read requests among multiple Read Servers using a round-robin approach, handles dynamic server registration, and integrates an LFUCache to improve latency and throughput.
- **Read Server**: Manages a subset of the key-value data and processes read requests from the Load Balancer. Supports dynamic scaling.
- **Client**: Interfaces with the system, allowing users to send read requests and receive responses.
- **LFUCache**: A custom cache implementation on the Load Balancer for storing and efficiently retrieving key-value pairs based on access frequency.

## Functionality
- **Read and Write Operations**: Clients can perform read or write operations on the key-value store.
- **Broadcast Write**: Write operations are broadcasted by the load balancer to all read servers to maintain consistency.
- **Dynamic Scalability**: New servers can be added to the network, with the load balancer facilitating data synchronization.
- **Hashing**: Round-Robin hashing ensures a balanced spread of read requests across the read servers.
- **LFU Cache**: Implemented LFU (Least Frequently Used) cache at the load balancer level to optimize performance. Infrequently accessed values are evicted from the cache, reducing communication overhead between the load balancer and read servers.

## Benefits
- **High Availability:**: Redundancy and data synchronization mechanisms ensure continuous availability of data.
- **Scalability**: Easily scale the system by adding new servers without disrupting existing operations.
- **Performance**: Consistent hashing and LFU caching optimize read operations, enhancing overall system performance.

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
   java ReadServer <LoadBalancerHost> <LoadBalancerPort> <ThisServerPort>
   ```
3. **Run the Client**:

   To send read requests and receive responses, start the Client by connecting to the Load Balancer:

   ```sh
   java Client <LoadBalancerHost> <LoadBalancerPort>
   ```
## Usage

After starting the Load Balancer, Read Servers, and the Client, utilize the Client's console to input keys. The system will then retrieve the corresponding value from the distributed storage, efficiently utilizing the LFUCache for optimized data access. This approach ensures quick retrieval times and scalability for read-heavy operations.



https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/46761328-2d36-4e45-bbd3-cd55dcb3a161


## Operations

Once you run the Client, LoadBalancer and ReadServer, the client can either read an existing value or write a new key-value. 

Reading a Value:

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/09aae299-16ea-4b26-87ed-36462f7db053)

Client received the value from Readserver1 running on port 8081:

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/0df60547-4b66-41fa-b34b-8bb1485994e3)

Writing new Values:

Currently, in our network we only have one ReadServer.

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/68a37aae-8ad1-4920-a332-09f36a1dac90)

Adding a new ReadServer which will register itself with Loadbalancer and ask for the latest key-value snapshots:

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/290ec4ea-5763-4fbf-b41d-1be095ed7b1f)

Even though we wrote the key value: E:Elephant before starting the ReadServer on 8082, we can see it is ReadServer on 8082 that is sending the requested value. This also shows that requests from the Client are directed to ReadServers in the network based on consistent hashing. 

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/a08874ff-41d6-4fe4-92d6-597dcadf9f1c)

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/d6c045f1-3ba2-48e9-8023-3e7735aaafa5)

ReadServer1(running on port 8081) is responsible for sending the value for key: D,A and ReadServer(running on port 8082) is reponsible for sending the value for key: E

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/3282d32a-7ecf-432c-b3ac-3e094224b9c9)

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/06eb03ba-6a02-4af6-8000-f70e07e40e21)

Now when we write new data, it is broadcasts again to all servers in the network i.e ReadServer1 and ReadServer2

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/949e2326-1449-40cf-83da-d9ef61fc396f)

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/0bdec725-ae7f-4b8e-9292-030e9b89941b)

![image](https://github.com/vedantipawar/Distributed-KeyValue-Storage/assets/51786206/1b7b4570-5dba-4d8a-bb69-f3084cea4db9)


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

