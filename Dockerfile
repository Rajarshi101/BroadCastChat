# Use the official OpenJDK image from the Docker Hub
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Compile the Java files
RUN javac BroadcastChatServer.java

# Command to run the server
CMD ["java", "BroadcastChatServer"]

# Expose the port that the server will run on
EXPOSE 5000
