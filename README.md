# 🚀 Instagram Comment-to-DM Automation

<div align="center">

![Instagram Automation](https://img.shields.io/badge/Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=next.js&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)

**Automate your Instagram engagement like a pro! 💬✨**

Turn comments into conversations instantly with smart keyword-triggered DMs.

[🎯 Features](#-features) • [🛠️ Setup](#️-quick-setup) • [📸 Demo](#-demo)

</div>

---

## 🎯 Features

✨ **Smart Automation**
- 🔍 Keyword detection in comments
- 💌 Instant DM responses
- 💬 Public comment replies
- 🎬 Per-reel customization

🎨 **Beautiful Dashboard**
- 📊 Real-time stats
- 🖼️ Visual reel grid
- ⚡ One-click configuration
- 🌈 Stunning glassmorphism UI

🔒 **Secure & Reliable**
- 🔐 Webhook verification
- 🛡️ Environment-based secrets
- 📝 JSON-based storage (no DB needed!)

---

## 🛠️ Quick Setup

### 📋 Prerequisites

- Instagram Business Account
- Meta Developer App
- Java 21+
- Node.js 18+

### 🔑 Get Your Credentials

1. **VERIFY_TOKEN**: Any random string (e.g., `my_secret_token_123`)
2. **INSTAGRAM_ACCESS_TOKEN**: 
   - Go to [Meta App Dashboard](https://developers.facebook.com/apps/)
   - Permissions needed: `instagram_business_basic`, `instagram_business_manage_messages`, `instagram_business_manage_comments`
3. **IG_BUSINESS_ACCOUNT_ID**: Found in Meta Business Suite.

---

## 💻 Local Development

### Backend Setup (Spring Boot)

```bash
cd backend
# Edit .env with your credentials
./mvnw spring-boot:run
```

🎉 Backend running at `http://localhost:8000`

### Frontend Setup (Next.js)

```bash
cd frontend
npm install
# Ensure .env.local has NEXT_PUBLIC_API_URL=http://localhost:8000
npm run dev
```

🎉 Frontend running at `http://localhost:3000`

---

## 📸 Demo

### Dashboard View
Beautiful, responsive dashboard to manage all your reels in one place.

### How It Works
1. 👤 User comments on your reel with trigger keyword (e.g., "info")
2. ⚡ Webhook instantly notifies your server
3. 💌 System sends personalized DM
4. 💬 Posts public reply on comment
5. 🎉 User gets instant response!

---

## 🎨 Tech Stack

| Technology | Purpose |
|------------|---------|
| 🍃 **Spring Boot** | Robust Java backend |
| ⚛️ **Next.js 14** | Modern React framework |
| 📘 **TypeScript** | Type-safe frontend |
| 🎨 **Tailwind CSS** | Beautiful, responsive UI |
| 📦 **JSON Storage** | Simple, no-database solution |

---

## 📄 License

MIT License - feel free to use this project for personal or commercial purposes!

---

## 💖 Credits

Built with ❤️ by **Tejas.algo** (Migrated to Spring Boot)
