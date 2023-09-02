- # General
    - #### Team#: Team 20
    
    - #### Names: Philip Gergis & Brian Bulgarelli
    
    - #### Project 5 Video Demo Link: https://www.youtube.com/watch?v=HHsroIFttF4&ab_channel=PhilipGergis

    - #### Instruction of deployment:
    AWS:
     
        1. Clone this repository using 'git clone https://github.com/uci-jherold2-teaching/cs122b-fall-team-20'
        2. CD into the repository, then run "mvn clean package" 
        3. Move the War file to your Tomcat Webapps by running "cp ./target/*.war /var/lib/tomcat9/webapps/"
        4. Confirm that the file is in the correct location by running "ls -lah /var/lib/tomcat9/webapps/"
        5. FabFlix should now be deployed on AWS.  
    
    Locally: 
    
        1. Clone this repository using 'git clone https://github.com/uci-jherold2-teaching/cs122b-fall-team-20'
        2. Open IntelliJ -> Import Project -> Choose the project you just cloned (The root path must contain the pom.xml!) -> Choose Import project from external model -> choose Maven -> Click on Finish -> The IntelliJ will load automatically
        3. For "Root Directory", right click "cs122b-fall-team-20" -> Mark Directory as -> sources root
        4. In WebContent/META-INF/context.xml, make sure the mysql username is mytestuser and password is mypassword
        5. Also make sure you have the moviedb database.
        
    - #### Collaborations and Work Distribution: We worked together on the entire project through peer programming and by working together on call.


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
	context.xml - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/WebContent/META-INF/context.xml
	AddMovieServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/AddMovieServlet.java
	AddStarServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/AddStarServlet.java
	AutocompleteServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/AutocompleteServlet.java
	MovieListServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/MovieListServlet.java
	DashLoginServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/DashLoginServlet.java
	MetaData.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/MetaData.java
	PaymentServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/PaymentServlet.java
	SingleMovieServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/SingleMovieServlet.java
	SingleStarServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/SingleStarServlet.java
	BrowseServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/BrowseServlet.java
	ConfirmationServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/ConfirmationServlet.java
	LoginServlet.java - https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/src/LoginServlet.java
	
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
	Before Connection Pooling, the Tomcat Servlet connected to our database using a url and then creating connections and running queries each time.
	As a result of making a connection and running queries every time, the Fablix website was slower than it could be. Now, Fabflix runs faster through
	connection pooling since we use the secure connection in the context.xml file that allows us to establish the datasource by allocation connections
	in the same file. The connections are retrieved and then it uses the connections from a group of pre-created ones, this way it does not need to make
	a new connection every time. In the end, we "close" the connection in the code but what it really does it put the connection back in the pre-created
	connections so they can be used again, making the website faster and more efficient.
    
    - #### Explain how Connection Pooling works with two backend SQL.
	Since the Master/Slave instances in the backend utilize the same codebase as the main instance, this allows the two backend servers to utilize 
	connection pooling, as described above, to save time when establishing and releasing connections.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
	- /src/Main.java
	- /WebContent/META-INF/context.xml

    - #### How read/write requests were routed to Master/Slave SQL?
	In our project we route write requests to the Master instance and allow read requests to either Master or Slave.
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
	You get the log file that you want in the same directory as log_processing.py. Then in the terminal run "python log_processing.py <YOUR TXT FILE>". 
	The file is in the "logging" directory.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         |                                     |                           | ??           |
| Case 2: HTTP/10 threads                        | https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/img/Single-HTTP-10.png   | 238                         | 13.353298870919765                                  | 13.150707755146772                        | We noticed that with HTTP, this performed better than the HTTP option with only one thread.           |
| Case 3: HTTPS/10 threads                       | https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/img/Single-HTTPS-10.png| 148                         | 9.666086045305025                   | 9.559174615580144         | We noticed that with HTTPS the average query time was lower, as well as the TJ and TS values.           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 402                         | 18.3284004652397058                                  | 18.125406785940302                        | Since this did not have connection pooling, it ran noticeably slower           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 102                         |  4.8583369363262628                                 | 4.7486713091964345                        | On the scaled version of HTTP with 1 thread we noticed better performance than the single machine            |
| Case 2: HTTP/10 threads                        | https://github.com/uci-jherold2-teaching/cs122b-fall-team-20/blob/main/img/Scaled-HTTP-10.png         | 92                         | 24.28534588697789                   | 24.101512513513512        | On the scaled version using HTTP and 10 threads, we noticed that the average query time was lower but the TS and TJ values increases by almost double.            |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |