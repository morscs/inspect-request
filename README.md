# inspect-request

Servlet utility class to support simple debugging and viewing of request contents from the 
server's perspective. Helpful for debugging when it is unclear whether a problem is caused by the 
reverse proxy configuration, the client request or the server layer.

## Configuration

Include the library in your deployment (recommended for test environments only):

        <dependency>
          <groupId>morscs.web</groupId>
          <artifactId>inspect-request</artifactId>
          <version>0.0.1</version>
          <packaging>jar</packaging>
        </dependency>

Register the servlet in your `web.xml`

        <servlet>
          <servlet-name>SimpleRequestInspectorServlet</servlet-name>
          <servlet-class>SimpleRequestInspectorServlet
          </servlet-class>
        </servlet>
        <servlet-mapping>
          <servlet-name>SimpleRequestInspectorServlet</servlet-name>
          <url-pattern>/inspect</url-pattern>
        </servlet-mapping>

## Usage

To view the output, deploy the WAR artifact and view this URL:

        http://localhost:8080/http-request-dump/inspect

Similar information should also be available in the server logs.

