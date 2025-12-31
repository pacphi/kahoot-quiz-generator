# Deploying to Fly.io

This guide provides step-by-step instructions for securely deploying the Kahoot Quiz Generator to [Fly.io](https://fly.io), including configuration for automatic scaling to zero to minimize costs.

## Quick Start

Already have Fly.io CLI installed? Deploy in 3 steps:

```bash
# 1. Install Fly CLI (if needed)
curl -L https://fly.io/install.sh | sh

# 2. Login to Fly.io
fly auth login

# 3. Deploy (first time - creates app and deploys)
fly launch

# Or deploy updates
fly deploy
```

**That's it!** Your app will be live at `https://your-app-name.fly.dev`

> **Note:** You'll need to set your API key as a secret:
> ```bash
> fly secrets set SPRING_AI_OPENAI_API_KEY=sk-proj-your-actual-key
> ```

For detailed instructions, troubleshooting, and advanced configuration, continue reading below.

## Table of Contents

- [Deploying to Fly.io](#deploying-to-flyio)
  - [Quick Start](#quick-start)
  - [Table of Contents](#table-of-contents)
  - [Prerequisites](#prerequisites)
    - [Required Software](#required-software)
    - [Required Accounts and Keys](#required-accounts-and-keys)
  - [Fly.io Account Setup](#flyio-account-setup)
    - [Sign Up](#sign-up)
    - [Install flyctl CLI](#install-flyctl-cli)
      - [macOS](#macos)
      - [Linux](#linux)
      - [Windows (PowerShell)](#windows-powershell)
    - [Authenticate](#authenticate)
  - [Prepare Application for Deployment](#prepare-application-for-deployment)
    - [Create Dockerfile](#create-dockerfile)
    - [Create .dockerignore](#create-dockerignore)
    - [Build Verification](#build-verification)
  - [Create fly.toml Configuration](#create-flytoml-configuration)
    - [Initialize Fly App](#initialize-fly-app)
    - [Configure Application](#configure-application)
    - [Configure Health Checks](#configure-health-checks)
    - [Configure Resources](#configure-resources)
  - [Setting Up Secrets Securely](#setting-up-secrets-securely)
    - [Set API Keys](#set-api-keys)
      - [For OpenAI](#for-openai)
      - [For Groq Cloud](#for-groq-cloud)
    - [Set Environment Variables](#set-environment-variables)
    - [Verify Secrets](#verify-secrets)
  - [Deploy to Fly.io](#deploy-to-flyio)
    - [Initial Deployment](#initial-deployment)
    - [Monitor Deployment](#monitor-deployment)
    - [Access Your Application](#access-your-application)
  - [Scaling Configuration](#scaling-configuration)
    - [Manual Scaling](#manual-scaling)
      - [Scale Machine Count](#scale-machine-count)
    - [VM Size Scaling](#vm-size-scaling)
    - [Scale to Zero](#scale-to-zero)
      - [Configuration in fly.toml](#configuration-in-flytoml)
      - [How It Works](#how-it-works)
      - [Benefits](#benefits)
      - [Monitoring Scale-to-Zero](#monitoring-scale-to-zero)
  - [Cost Optimization](#cost-optimization)
    - [Scale-to-Zero Benefits](#scale-to-zero-benefits)
    - [Understanding Cold Starts](#understanding-cold-starts)
    - [Cost Estimates](#cost-estimates)
    - [Additional Cost Optimization Tips](#additional-cost-optimization-tips)
  - [Automated Deployment with GitHub Actions](#automated-deployment-with-github-actions)
    - [Setup Instructions](#setup-instructions)
    - [How It Works](#how-it-works-1)
    - [Manual Deployment](#manual-deployment)
  - [Private Networking with Fly Proxy](#private-networking-with-fly-proxy)
    - [How It Works](#how-it-works-2)
    - [Configuration](#configuration)
      - [Step 1: Remove Public HTTP Service](#step-1-remove-public-http-service)
      - [Step 2: Deploy with Private Configuration](#step-2-deploy-with-private-configuration)
      - [Step 3: Access via Fly Proxy](#step-3-access-via-fly-proxy)
    - [Access Management](#access-management)
      - [Who Can Access?](#who-can-access)
      - [Granting Team Access](#granting-team-access)
      - [Revoking Access](#revoking-access)
    - [Common Use Cases](#common-use-cases)
    - [Advanced: Hybrid Public + Private](#advanced-hybrid-public--private)
    - [Monitoring Private Apps](#monitoring-private-apps)
    - [Troubleshooting Private Networking](#troubleshooting-private-networking)
      - [Error: "Connection refused"](#error-connection-refused)
      - [Error: "App not found"](#error-app-not-found)
      - [Slow Connection](#slow-connection)
    - [Limitations of Fly Proxy](#limitations-of-fly-proxy)
  - [Post-Deployment](#post-deployment)
    - [Custom Domains](#custom-domains)
    - [SSL Certificates](#ssl-certificates)
    - [Monitoring and Metrics](#monitoring-and-metrics)
  - [Troubleshooting](#troubleshooting)
    - [Common Deployment Errors](#common-deployment-errors)
      - [Error: "Could not find a Dockerfile"](#error-could-not-find-a-dockerfile)
      - [Error: "Build failed"](#error-build-failed)
    - [Health Check Failures](#health-check-failures)
      - [Symptom: Deployment succeeds but app shows "unhealthy"](#symptom-deployment-succeeds-but-app-shows-unhealthy)
      - [Symptom: "Connection refused" in health checks](#symptom-connection-refused-in-health-checks)
    - [Memory Issues](#memory-issues)
      - [Symptom: "Out of memory" or frequent restarts](#symptom-out-of-memory-or-frequent-restarts)
      - [Symptom: Slow startup times](#symptom-slow-startup-times)
    - [Log Debugging](#log-debugging)
  - [Useful Commands Reference](#useful-commands-reference)
  - [Next Steps](#next-steps)

## Prerequisites

### Required Software

Before deploying, ensure you have completed the build process (see [BUILD.md](BUILD.md)) and have the following installed:

- **flyctl CLI** - Fly.io command-line tool
- **Docker** - For local testing (optional but recommended)
- **Git** - For version control

### Required Accounts and Keys

- **Fly.io Account** - Free tier available
- **LLM Provider API Key** - Either:
  - [OpenAI API Key](https://platform.openai.com/api-keys), or
  - [Groq Cloud API Key](https://console.groq.com/keys)

## Fly.io Account Setup

### Sign Up

1. Visit [https://fly.io/app/sign-up](https://fly.io/app/sign-up)
2. Sign up using GitHub, Google, or email
3. Verify your email address
4. Add a payment method (required even for free tier, but you won't be charged unless you exceed free tier limits)

### Install flyctl CLI

#### macOS

```bash
brew install flyctl
```

#### Linux

```bash
curl -L https://fly.io/install.sh | sh
```

#### Windows (PowerShell)

```powershell
pwsh -Command "iwr https://fly.io/install.ps1 -useb | iex"
```

For other installation methods, see [Fly.io Docs: Install flyctl](https://fly.io/docs/hands-on/install-flyctl/).

### Authenticate

```bash
fly auth login
```

This will open a browser window for authentication. After logging in, verify:

```bash
fly auth whoami
```

## Prepare Application for Deployment

### Create Dockerfile

A production-ready `Dockerfile` is included in the project root ([`Dockerfile`](../Dockerfile)).

**Key features:**

- **Multi-stage build** - Separates build and runtime stages for smaller final image
- **BellSoft Liberica Alpine JDK** - Lightweight Alpine Linux-based Java distribution
  - Builder stage: `bellsoft/liberica-openjdk-alpine:25`
  - Runtime stage: `bellsoft/liberica-openjre-alpine:25` (JRE only for smaller size)
- **Spring Boot layer extraction** - Optimizes Docker caching and rebuild times
- **Security best practices** - Runs as non-root user (`appuser`)
- **Health checks** - Monitors `/actuator/health` endpoint
- **Minimal image size** - Alpine Linux base (~200MB vs ~400MB for Ubuntu-based images)

**Documentation:**

- [BellSoft Liberica Alpine Images](https://hub.docker.com/r/bellsoft/liberica-openjdk-alpine)
- [Spring Boot Docker Documentation](https://spring.io/guides/topicals/spring-boot-docker/)

### Create .dockerignore

A `.dockerignore` file is included in the project root ([`.dockerignore`](../.dockerignore)) to exclude unnecessary files from the Docker build context.

**Excluded files:**

- Build artifacts (`target/`, `node_modules/`, `dist/`)
- Local configuration (`config/`, log files)
- IDE files (`.idea/`, `.vscode/`, etc.)
- Documentation (markdown files, `docs/`)
- Version control (`.git/`)

This significantly reduces build context size and speeds up Docker builds.

### Build Verification

Test the Docker build locally before deploying:

```bash
# Build the Docker image
docker build -t kahoot-quiz-generator .

# Run locally to test
docker run -p 8080:8080 \
  -e SPRING_AI_OPENAI_API_KEY=your-api-key \
  kahoot-quiz-generator

# Test in browser
open http://localhost:8080
```

## Create fly.toml Configuration

### Initialize Fly App

Initialize a new Fly.io app (this creates a basic `fly.toml`):

```bash
fly launch --no-deploy
```

You'll be prompted to:

- **App name**: Choose a unique name (e.g., `kahoot-quiz-gen-yourname`)
- **Region**: Select closest to your users (e.g., `iad` for US East)
- **Database**: Select "No" - this app doesn't need a database
- **Deploy now**: Select "No" - we'll configure first

### Configure Application

Edit the generated `fly.toml` file with the following configuration:

```toml
# fly.toml app configuration file
# See https://fly.io/docs/reference/configuration/

app = "kahoot-quiz-gen"  # Replace with your app name
primary_region = "iad"    # Replace with your chosen region

# Build configuration
[build]
  dockerfile = "Dockerfile"

# HTTP service configuration
[http_service]
  internal_port = 8080
  force_https = true
  auto_stop_machines = "stop"
  auto_start_machines = true
  min_machines_running = 0
  processes = ["app"]

  # Concurrency limits
  [http_service.concurrency]
    type = "requests"
    hard_limit = 250
    soft_limit = 200

# Health check configuration
[[http_service.checks]]
  interval = "30s"
  timeout = "5s"
  grace_period = "10s"
  method = "GET"
  path = "/actuator/health"
  protocol = "http"

  [http_service.checks.headers]
    Accept = "application/json"

# VM resources
[vm]
  memory = "2gb"  # 2GB recommended for Java Spring Boot applications
  cpu_kind = "shared"
  cpus = 1

# Environment variables (non-sensitive)
[env]
  SPRING_PROFILES_ACTIVE = "openai"  # or "groq-cloud"
  SERVER_PORT = "8080"
  JAVA_TOOL_OPTIONS = "-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"
```

**Documentation References:**

- [fly.toml Reference](https://fly.io/docs/reference/configuration/)
- [Health Checks](https://fly.io/docs/reference/configuration/#services-http_checks)
- [Autoscaling](https://fly.io/docs/reference/configuration/#services-autoscaling)

### Configure Health Checks

The `/actuator/health` endpoint from Spring Boot Actuator provides health check information. This is already included in the dependencies and configured in `application.yml`.

The health check configuration in `fly.toml`:

- **interval**: How often to check (30s)
- **timeout**: Max time to wait for response (5s)
- **grace_period**: Time before first check after start (10s)
- **path**: Health check endpoint

### Configure Resources

The `[vm]` section configures compute resources:

- **memory**: 2GB recommended for Java Spring Boot apps (minimum 1GB)
- **cpu_kind**: "shared" for cost-effective CPU
- **cpus**: 1 CPU is sufficient for low-moderate traffic

Adjust based on your needs. See [Fly.io Pricing](https://fly.io/docs/about/pricing/) for cost estimates.

## Setting Up Secrets Securely

**IMPORTANT:** Never commit secrets to version control. Use Fly.io's secrets management.

### Set API Keys

#### For OpenAI

```bash
fly secrets set SPRING_AI_OPENAI_API_KEY=sk-proj-your-actual-api-key-here
```

#### For Groq Cloud

```bash
fly secrets set SPRING_AI_OPENAI_API_KEY=gsk_your-actual-groq-api-key-here
fly secrets set CHAT_MODEL=llama-3.3-70b-versatile
```

> **Note:** Spring AI for Groq uses the OpenAI-compatible API, so use `SPRING_AI_OPENAI_API_KEY` with the Groq base URL configured in the `groq-cloud` profile.

### Set Environment Variables

If using Groq Cloud, update `fly.toml`:

```toml
[env]
  SPRING_PROFILES_ACTIVE = "groq-cloud"
  SERVER_PORT = "8080"
  JAVA_TOOL_OPTIONS = "-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"
```

### Verify Secrets

List configured secrets (values are redacted):

```bash
fly secrets list
```

Output:

```text
NAME                          DIGEST                  CREATED AT
SPRING_AI_OPENAI_API_KEY     a1b2c3d4e5f6           2025-01-15T12:34:56Z
```

## Deploy to Fly.io

### Initial Deployment

Deploy your application:

```bash
fly deploy
```

This will:

1. Build the Docker image
2. Push to Fly.io's registry
3. Create and start VM instances
4. Run health checks
5. Route traffic once healthy

### Monitor Deployment

Watch deployment progress:

```bash
fly logs
```

Look for:

```text
Started KahootQuizGeneratorApplication in X.XXX seconds
```

### Access Your Application

Once deployed, your app is available at:

```url
https://your-app-name.fly.dev
```

Check status:

```bash
fly status
```

Open in browser:

```bash
fly open
```

## Scaling Configuration

### Manual Scaling

#### Scale Machine Count

Add more VM instances for high availability:

```bash
# Scale to 2 instances
fly scale count 2

# Scale specific regions
fly scale count 2 --region iad
fly scale count 1 --region syd
```

### VM Size Scaling

Change VM resources:

```bash
# List available VM sizes
fly platform vm-sizes

# Scale to larger VM
fly scale vm shared-cpu-2x --memory 2048

# Scale to performance VM
fly scale vm performance-2x --memory 4096
```

### Scale to Zero

**Scale to zero** automatically stops VMs when idle and restarts them on incoming requests, significantly reducing costs.

#### Configuration in fly.toml

The configuration is already included in the example above:

```toml
[http_service]
  auto_stop_machines = "stop"  # Stop machines when idle
  auto_start_machines = true    # Restart on request
  min_machines_running = 0      # Allow scaling to zero
```

#### How It Works

1. **Idle Detection**: After 5 minutes of no requests, machines automatically stop
2. **Auto-Start**: On incoming request, Fly.io:
   - Starts a stopped machine
   - Waits for health check to pass
   - Routes traffic to the machine
3. **Cold Start**: First request after stop takes 10-30 seconds (Java startup time)

#### Benefits

- **Cost Savings**: Only pay for actual usage
- **Free Tier Friendly**: 3 shared-cpu-1x VMs with 256MB RAM included
- **Automatic Management**: No manual intervention needed

#### Monitoring Scale-to-Zero

```bash
# Check machine status
fly status

# View machine list
fly machines list

# See which machines are stopped
fly machines list --json | jq '.[] | {id, state}'
```

> **Documentation:** [Fly.io Autoscaling and Autostopping](https://fly.io/docs/apps/autostart-stop/)

## Cost Optimization

Fly.io offers excellent cost optimization features, especially for applications with variable or low traffic patterns.

### Scale-to-Zero Benefits

With scale-to-zero configuration already included in your `fly.toml`, you'll benefit from:

**Cost Savings:**

- **Pay only for usage**: Machines stop after 5 minutes of inactivity
- **Free tier friendly**: Fly.io includes 3 shared-cpu-1x VMs with 256MB RAM
- **Automatic management**: No manual intervention needed
- **Estimated costs**:
  - Light usage (mostly idle): ~$0-5/month
  - Moderate usage (occasional active): ~$5-15/month
  - Heavy usage (frequently active): ~$15-30/month with 2GB RAM

**How It Works:**

1. Machine automatically stops after 5 minutes of no requests
2. On incoming request, Fly.io starts the machine
3. Health check passes (waits for Spring Boot to fully start)
4. Traffic is routed to the healthy machine

### Understanding Cold Starts

**What is a cold start?**
When a machine is stopped and receives a request, it must start up before serving traffic. For this Spring Boot application:

- **Cold start time**: ~20-40 seconds
  - Docker container start: ~2-5 seconds
  - Spring Boot initialization: ~15-30 seconds
  - Health check validation: ~3-5 seconds

**Minimizing cold start impact:**

1. **Keep machines warm for critical hours:**

   ```bash
   # Temporarily prevent auto-stop (for business hours)
   fly scale count 1 --yes

   # Re-enable auto-stop later
   fly scale count 0 --yes
   ```

2. **Optimize Spring Boot startup:**
   - The `fly.toml` already includes JVM optimization flags
   - Consider using Spring Boot 3's native image (GraalVM) for ~2s startup
   - Enable Class Data Sharing (CDS) for faster JVM initialization

3. **Monitor cold starts:**

   ```bash
   # Check machine state
   fly status

   # View startup logs
   fly logs --filter "Started KahootQuizGeneratorApplication"
   ```

### Cost Estimates

**Current Configuration** (2GB RAM, shared CPU, scale-to-zero):

| Usage Pattern | Active Hours/Month | Estimated Cost |
|---------------|-------------------|----------------|
| **Personal/Demo** | 10-20 hours | $2-5/month |
| **Small team** | 40-80 hours | $8-15/month |
| **Active development** | 100-200 hours | $20-40/month |
| **Always-on (no scaling)** | 730 hours | ~$30-35/month |

**Cost Breakdown:**

- Compute: ~$0.04/hour for 2GB shared-cpu-1x
- Bandwidth: First 160GB/month free
- Storage: Ephemeral only (no cost)

**Free Tier Allowances:**

- 3 shared-cpu-1x VMs with 256MB RAM (note: this app uses 2GB)
- 160GB outbound data transfer
- Unlimited inbound bandwidth

> **Tip:** Monitor your usage at [Fly.io Dashboard](https://fly.io/dashboard) to track costs in real-time.

### Additional Cost Optimization Tips

1. **Use appropriate memory:**

   ```bash
   # Current: 2GB (recommended for Java)
   # If you find 2GB is excessive, you can scale down:
   fly scale memory 1024  # 1GB

   # Monitor memory usage first:
   fly dashboard metrics
   ```

2. **Optimize regions:**

   ```bash
   # Deploy only in regions where you have users
   fly regions list
   fly regions set lax sjc  # West Coast USA only
   ```

3. **Review logs regularly:**

   ```bash
   # Check for errors that might cause restarts
   fly logs --level error
   ```

4. **Consider alternatives for very low traffic:**
   - For < 10 hours/month usage: Scale-to-zero is optimal
   - For 24/7 availability: Keep 1 instance running
   - For high availability: Run 2+ instances with load balancing

## Automated Deployment with GitHub Actions

Automate deployments to Fly.io using GitHub Actions. Every push to the `main` branch will automatically deploy your application.

### Setup Instructions

A GitHub Actions workflow (`.github/workflows/fly-deploy.yml`) is already included in the repository. To enable it:

**Step 1: Get your Fly.io API token**

```bash
# Login to Fly.io
fly auth login

# Get your API token
fly auth token
```

Copy the token that is displayed.

**Step 2: Add token to GitHub Secrets**

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Name: `FLY_API_TOKEN`
5. Value: Paste your Fly.io token
6. Click **Add secret**

**Step 3: Deploy**

That's it! Now every push to `main` will automatically deploy:

```bash
git add .
git commit -m "Update application"
git push origin main

# GitHub Actions will automatically deploy to Fly.io
```

### How It Works

The workflow (`.github/workflows/fly-deploy.yml`) does the following:

1. **Triggers** on push to `main` branch
2. **Checks out** your code
3. **Sets up** Fly CLI tools
4. **Deploys** using `flyctl deploy --remote-only`
   - Builds Docker image on Fly.io's infrastructure (faster, no local Docker needed)
   - Pushes to Fly.io's registry
   - Deploys to your app

**Concurrency Control:**

- Only one deployment runs at a time (via `concurrency: deploy-group`)
- If you push multiple commits quickly, only the latest will deploy

**View Deployment Status:**

1. Go to your GitHub repository
2. Click **Actions** tab
3. See real-time deployment logs

### Manual Deployment

You can still deploy manually when needed:

```bash
# Deploy from your local machine
fly deploy

# Deploy specific directory
fly deploy --config fly.toml

# Deploy with local Docker build (slower but more control)
fly deploy --local-only

# Deploy to specific app
fly deploy --app your-app-name
```

**When to use manual deployment:**

- Testing changes before committing
- Deploying from a branch other than `main`
- Troubleshooting deployment issues
- Deploying to staging/test environments

## Private Networking with Fly Proxy

For maximum security, you can configure your application to be accessible **only** via authenticated `fly proxy` connections. This prevents public internet access and restricts the app to users with Fly.io account credentials.

### How It Works

**Fly Proxy** creates a secure tunnel from your local machine to your Fly.io app:

- ✅ **Authentication Required**: Only users logged into `flyctl` with proper org access can connect
- ✅ **No Public Exposure**: App is not accessible via public internet or `.fly.dev` domain
- ✅ **End-to-End Encryption**: Traffic is encrypted through Fly's private network (6PN)
- ✅ **Zero Additional Cost**: No extra charges for private networking

### Configuration

#### Step 1: Remove Public HTTP Service

Edit your `fly.toml` to **remove or comment out** the `[http_service]` section:

```toml
# fly.toml app configuration file
app = "kahoot-quiz-gen"
primary_region = "iad"

[build]
  dockerfile = "Dockerfile"

# REMOVED: [http_service] section for private-only access
# The app will only be accessible via fly proxy

# VM resources
[vm]
  memory = "2gb"
  cpu_kind = "shared"
  cpus = 1

# Environment variables
[env]
  SPRING_PROFILES_ACTIVE = "openai"
  SERVER_PORT = "8080"
  JAVA_TOOL_OPTIONS = "-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

# Health checks still work internally
[[services.tcp_checks]]
  interval = "15s"
  timeout = "2s"
  grace_period = "10s"
```

#### Step 2: Deploy with Private Configuration

```bash
# Deploy with private networking only
fly deploy
```

After deployment, your app will **not** have a public URL. Attempting to access `https://your-app.fly.dev` will fail.

#### Step 3: Access via Fly Proxy

Create a secure tunnel to your private app:

```bash
# Forward local port 8080 to app's port 8080
fly proxy 8080:8080

# Or use a different local port
fly proxy 3000:8080
```

Then access in your browser:

```url
http://localhost:8080
```

The connection is authenticated using your `fly auth login` credentials.

### Access Management

#### Who Can Access?

Only users who:

1. Have a Fly.io account
2. Are authenticated via `fly auth login`
3. Have access to your Fly.io organization (for team apps)

#### Granting Team Access

For team deployments:

```bash
# Add team member to your organization
fly orgs invite your-org-name user@example.com

# Verify members
fly orgs members
```

Team members must:

```bash
# Authenticate
fly auth login

# Connect to app
fly proxy 8080:8080 --app kahoot-quiz-gen
```

#### Revoking Access

```bash
# Remove user from organization
fly orgs revoke your-org-name user@example.com
```

### Common Use Cases

| Scenario | Recommended Setup |
|----------|-------------------|
| **Personal project** | Fly proxy (private) |
| **Internal company tool** | Fly proxy + team org access |
| **Small team (< 10 users)** | Fly proxy with shared org |
| **Public demo/portfolio** | Public `[http_service]` |
| **API with auth** | Public with API key/JWT middleware |

### Advanced: Hybrid Public + Private

You can run **two separate deployments**:

```bash
# Public demo (read-only)
fly apps create kahoot-quiz-gen-demo
# Deploy with [http_service] enabled

# Private admin (full access)
fly apps create kahoot-quiz-gen-admin
# Deploy without [http_service], access via proxy
```

### Monitoring Private Apps

Even with private networking, you can still:

```bash
# View logs
fly logs

# Check status
fly status

# Open dashboard
fly dashboard

# SSH into machine
fly ssh console
```

### Troubleshooting Private Networking

#### Error: "Connection refused"

```bash
# Verify app is running
fly status

# Check machine state
fly machines list

# If stopped, start manually
fly machines start <machine-id>
```

#### Error: "App not found"

```bash
# Specify app explicitly
fly proxy 8080:8080 --app kahoot-quiz-gen

# Or set current app
fly apps list
```

#### Slow Connection

```bash
# Check your region and app region match
fly regions list

# Add machine in your region
fly machines clone --region ord
```

### Limitations of Fly Proxy

- **No .fly.dev domain**: App won't have a public URL
- **Local connection required**: Each user must run `fly proxy`
- **Cold starts**: Scale-to-zero still applies, first request may be slow
- **No CDN**: No global edge caching (but usually fine for internal tools)

> **Documentation:** [Fly.io Private Networking](https://fly.io/docs/networking/private-networking/)

## Post-Deployment

### Custom Domains

Add a custom domain:

```bash
fly certs add yourdomain.com
```

Then add DNS records:

- Type: `A`, Name: `@`, Value: (IP from fly certs show)
- Type: `AAAA`, Name: `@`, Value: (IPv6 from fly certs show)

Verify:

```bash
fly certs show yourdomain.com
```

### SSL Certificates

SSL certificates are **automatically provisioned** via Let's Encrypt. No configuration needed!

Check certificate status:

```bash
fly certs check yourdomain.com
```

### Monitoring and Metrics

View application metrics:

```bash
# Live logs
fly logs

# Metrics dashboard
fly dashboard metrics

# Health status
fly status
```

Spring Boot Actuator endpoints are available at:

- Health: `https://your-app.fly.dev/actuator/health`
- Metrics: `https://your-app.fly.dev/actuator/metrics`
- Info: `https://your-app.fly.dev/actuator/info`

## Troubleshooting

### Common Deployment Errors

#### Error: "Could not find a Dockerfile"

**Solution:**

```bash
# Ensure Dockerfile exists in project root
ls -la Dockerfile

# Or specify path in fly.toml
[build]
  dockerfile = "./Dockerfile"
```

#### Error: "Build failed"

**Solution:**

```bash
# Check build logs
fly logs

# Test Docker build locally
docker build -t test .

# Ensure all files are committed
git status
```

### Health Check Failures

#### Symptom: Deployment succeeds but app shows "unhealthy"

**Solution:**

```bash
# Check health endpoint locally
curl https://your-app.fly.dev/actuator/health

# View detailed logs
fly logs

# Increase grace period in fly.toml
[[http_service.checks]]
  grace_period = "60s"  # Increase for slow Java startup
```

#### Symptom: "Connection refused" in health checks

**Solution:**

- Ensure app binds to `0.0.0.0`, not `localhost`
- Verify `SERVER_PORT=8080` in fly.toml
- Check app is listening on correct port in logs

### Memory Issues

#### Symptom: "Out of memory" or frequent restarts

**Solution:**

```bash
# With 2GB configured, memory issues should be rare
# If you still encounter them, increase VM memory:
fly scale vm shared-cpu-1x --memory 4096  # Upgrade to 4GB

# Optimize Java heap in fly.toml (already configured)
[env]
  JAVA_TOOL_OPTIONS = "-XX:MaxRAMPercentage=75.0 -Xss256k -XX:+UseContainerSupport"

# Monitor memory usage
fly dashboard metrics
```

#### Symptom: Slow startup times

**Solution:**

- Enable Class Data Sharing (CDS):

  ```bash
  JAVA_TOOL_OPTIONS = "-Xshare:on -XX:MaxRAMPercentage=75.0"
  ```

- Consider using GraalVM native image (requires code changes)

### Log Debugging

View real-time logs:

```bash
# All logs
fly logs

# Filter by level
fly logs --level error

# Last 100 lines
fly logs --lines 100

# Specific instance
fly logs --instance 01234567890abc
```

Common log patterns:

- `Started KahootQuizGeneratorApplication` - Successful startup
- `OpenAI API key must be set` - Missing secret
- `Port 8080 already in use` - Port configuration issue

## Useful Commands Reference

| Command | Description |
|---------|-------------|
| `fly launch` | Initialize new Fly app |
| `fly deploy` | Deploy application |
| `fly open` | Open app in browser |
| `fly logs` | Stream application logs |
| `fly status` | Show app status |
| `fly dashboard` | Open web dashboard |
| `fly secrets list` | List configured secrets |
| `fly secrets set KEY=value` | Set secret |
| `fly secrets unset KEY` | Remove secret |
| `fly scale count N` | Scale to N instances |
| `fly scale vm SIZE` | Change VM size |
| `fly machines list` | List VM instances |
| `fly ssh console` | SSH into running instance |
| `fly postgres create` | Create Postgres DB (if needed) |
| `fly certs add DOMAIN` | Add custom domain |
| `fly certs show DOMAIN` | Show certificate status |
| `fly releases` | Show deployment history |
| `fly deploy --local-only` | Build locally instead of remote |
| `fly destroy` | Delete app (careful!) |

For complete command reference, see [Fly.io Docs: flyctl](https://fly.io/docs/flyctl/).

---

## Next Steps

- **Monitor Costs**: Check [Fly.io Dashboard](https://fly.io/dashboard) regularly
- **Set Up Alerts**: Configure alerts for errors and downtime
- **Performance Testing**: Load test with expected traffic patterns
- **Backup Strategy**: Although stateless, document configuration
- **CI/CD Integration**: Automate deployments via GitHub Actions (see [CI.md](CI.md))

For questions or issues, refer to:

- [Fly.io Community](https://community.fly.io/)
- [Fly.io Documentation](https://fly.io/docs/)
- [Spring Boot on Fly.io](https://fly.io/docs/languages-and-frameworks/java/)
