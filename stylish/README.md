# STYLiSH

It's an e-commerce site developed by a Java learner.

## Website URL

[http://13.54.172.208/](http://13.54.172.208)

## Author

Terry Cheng

## Tech

### Back-end

- Java
  - Main programming language.
  - Back-end architecture.
- Spring boot
  - Back-end framework.

### Front-end

- HTML
  - Front-end components and content.
- CSS
  - Front-end style.
- Javascript
  - Front-end interction and simple functions.

## Start the Web Server on Port 80

Using Nginx as a reverse proxy to forward requests from port 80 to Spring boot application which orignally running on port 8080.

1. **Configure Nginx**:

- Edit config file `/etc/nginx/sites-available/default` by following:

  ```nginx
  server {
    listen 80 default_server;
    listen [::]:80 default_server;

    server_name _;

    location / {
      // add
      proxy_pass http://localhost:8080;
      proxy_set_header Host $host;
      proxy_set_header X-Real_IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto $scheme;
    }
  }
  ```

2. **Restart Nginx**:

- To apply the changes, restart Nginx:

  ```bash
  sudo systemctl restart nginx
  ```  

## Working Flow

To automactically deploy to EC2, I've set up GitHub Actions workflow. Every time code publish to a branch start with 'w', `deploy-to-ec2.yml` will:

1. Check out the code from repo.
2. Set up SSH and copy project files to EC2 instance.
3. SSH into EC2 instance and build the project, then restart Spring Boot application.

## Run Web Server in Background

To keep web server running in background, even after closing SSH connection, I use nohup command. This command runs Spring Boot project in background.

  ```bash
  nohup java -jar home/ubuntu/stylish/demo/target/demo-0.0.1-SNAPSHOT.jar > home/ubuntu/nohup.out 2>&1 &
  ```
