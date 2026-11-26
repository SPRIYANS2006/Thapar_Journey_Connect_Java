# Thapar Journey Connect — Cloud Deployment Guide
**Campus Transit & Travel Cohort Operating System**  
*Production Deployment Options & Step-by-Step Instructions*

---

## Architecture Overview for Deployment

Our application is architected as a **Single Self-Contained Full-Stack Artifact**:
* The React 19 production build is pre-embedded inside Spring Boot's `resources/static/` directory.
* The Spring Boot Tomcat web server serves **both** the REST API endpoints (`/api/*`) and the React Single Page Application (`/`, `/dashboard`, etc.) from a **single port**.
* This eliminates CORS issues and means you only need to deploy **one service** rather than managing separate frontend and backend hosting.

---

## Option 1: Render.com (Recommended — Easiest & Free)

Render provides free hosting for web services and automated builds directly from your GitHub repository.

### Steps:
1. **Push your code to GitHub**:
   Create a new GitHub repository (e.g., `thapar-journey-connect`) and push the contents of `Thapar_Journey_Connect_Java`.

2. **Create a Free Account on [Render.com](https://render.com/)**:
   * Click **New +** $\rightarrow$ **Web Service**.
   * Connect your GitHub repository.

3. **Configure the Web Service**:
   * **Name**: `thapar-journey-connect`
   * **Region**: Singapore or Frankfurt (closest to India)
   * **Environment**: `Docker`
   * **Branch**: `main`
   * **Dockerfile Path**: `./Dockerfile`
   * **Plan**: Free

4. **Environment Variables**:
   * `PORT`: `8080` (Render binds automatically, but setting it is good practice)
   * `APP_JWT_SECRET`: `thapar-journey-connect-jwt-secret-academic-lab-2026-key-32bytes-min`

5. **Deploy**:
   * Click **Create Web Service**.
   * Render will automatically execute the multi-stage `Dockerfile`, build the React frontend, package the Spring Boot JAR, and launch the service.
   * Your app will be live at: `https://thapar-journey-connect.onrender.com`.

---

## Option 2: Railway.app (Fast 1-Click Deployment)

Railway provides $5 free monthly credit and fast containerized deployments:

1. Go to [railway.app](https://railway.app/) and sign in with GitHub.
2. Click **New Project** $\rightarrow$ **Deploy from GitHub repo**.
3. Select your repository.
4. Railway will automatically detect the `Dockerfile` and begin building.
5. In **Settings** $\rightarrow$ **Networking**, click **Generate Domain** (e.g. `thapar-journey-connect.up.railway.app`).
6. Your application is live immediately with automatic HTTPS.

---

## Option 3: VPS Deployment (Ubuntu / AWS EC2 / Oracle Cloud Free Tier)

If deploying to an Ubuntu virtual machine:

### 1. Install Java 21 on the Server:
```bash
sudo apt update
sudo apt install -y openjdk-21-jre-headless
```

### 2. Transfer the Pre-Built JAR:
Upload `backend/target/thapar-journey-connect-backend-1.0.0.jar` to `/opt/thapar-journey/app.jar`.

### 3. Setup a Systemd Service (Auto-restart on reboot):
Create `/etc/systemd/system/thapar-journey.service`:
```ini
[Unit]
Description=Thapar Journey Connect Spring Boot Application
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/opt/thapar-journey
ExecStart=/usr/bin/java -jar /opt/thapar-journey/app.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable thapar-journey
sudo systemctl start thapar-journey
```

### 4. Enable Free SSL with Caddy (Recommended) or Nginx:
Install Caddy:
```bash
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update
sudo apt install caddy
```

In `/etc/caddy/Caddyfile`:
```caddy
yourdomain.com {
    reverse_proxy localhost:8080
}
```
Reload Caddy:
```bash
sudo systemctl reload caddy
```
Caddy will automatically fetch a free Let's Encrypt SSL certificate and serve HTTPS on port 443!

---

## Database Configuration (MongoDB Atlas Cloud Cluster)

The application uses **MongoDB Atlas** for its persistent database:

### Environment Variables:
Set these environment variables in your Render/cloud dashboard:
* `SPRING_DATA_MONGODB_URI` = `mongodb+srv://<username>:<password>@<cluster>.mongodb.net/thapar_journey?retryWrites=true&w=majority`
* `SPRING_DATA_MONGODB_DATABASE` = `thapar_journey`

Hibernate will automatically create all tables (`ddl-auto=update`) and `DatabaseSeederService` will populate the default TIET master data on first launch!
