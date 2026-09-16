# Security Policy

## Supported Versions

We provide security updates and patches for the following versions of the **GATE Papers** Android application:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0.0 | :x:                |

---

## Reporting a Vulnerability

Security and data privacy are taken seriously in GATE Papers. If you discover a security vulnerability or sensitive issue, please report it privately rather than opening a public issue.

### How to Report
Please email our security maintainer directly:
- **Contact**: Pushpak Jaiswal
- **Email**: [pushpakmjaiswal@gmail.com](mailto:pushpakmjaiswal@gmail.com)
- **Subject Line**: `[SECURITY] Vulnerability Report - GATE Papers`

### Please Include:
1. A description of the vulnerability and its potential impact.
2. Step-by-step instructions or proof-of-concept to reproduce the issue.
3. The app version, device model, and Android OS version tested.
4. Any proposed mitigation or fix if available.

### What to Expect:
- **Acknowledgement**: Within 48 hours of your report.
- **Assessment & Triage**: We will confirm the vulnerability and determine the severity.
- **Resolution**: We will provide a timeline for fixing and deploying a patched release APK.
- **Attribution**: You will be credited in our release notes (unless you prefer anonymity).

---

## Data Privacy & Device Security Architecture
- **Zero Tracker Policy**: GATE Papers does not bundle analytics trackers, adware SDKs, or user tracking services.
- **Local SQLite Database**: All completion states and notes stay strictly on the user's device in local Room database storage.
- **Clean Network Calls**: The app only connects over secure HTTPS to fetch official GATE PDF papers and paper indices from verified GitHub repository raw endpoints.

Thank you for helping keep GATE Papers safe and trustworthy for everyone!
