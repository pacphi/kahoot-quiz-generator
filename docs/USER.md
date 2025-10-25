# User Guide - Kahoot Quiz Generator

## Table of Contents

- [Introduction](#introduction)
- [Who Is This For?](#who-is-this-for)
- [Prerequisites](#prerequisites)
- [Quick Start Guide](#quick-start-guide)
  - [Step 1: Access the Application](#step-1-access-the-application)
- [Mode 1: AI Generation](#mode-1-ai-generation)
  - [Step 1: Enter Your Quiz Topic](#step-1-enter-your-quiz-topic)
  - [Step 2: Choose Number of Questions](#step-2-choose-number-of-questions)
  - [Step 3: Generate Your Quiz](#step-3-generate-your-quiz)
- [Mode 2: CSV Upload](#mode-2-csv-upload)
  - [Step 1: Prepare Your CSV File](#step-1-prepare-your-csv-file)
  - [Step 2: Upload Your CSV File](#step-2-upload-your-csv-file)
  - [Step 3: Review the Preview](#step-3-review-the-preview)
  - [Step 4: Configure Conversion Options](#step-4-configure-conversion-options)
  - [Step 5: Generate Kahoot Quiz](#step-5-generate-kahoot-quiz)
- [Understanding Your Output](#understanding-your-output)
  - [What's in the Excel File?](#whats-in-the-excel-file)
  - [File Format](#file-format)
- [Uploading to Kahoot](#uploading-to-kahoot)
  - [Step 1: Log into Kahoot](#step-1-log-into-kahoot)
  - [Step 2: Import Your Quiz](#step-2-import-your-quiz)
  - [Step 3: Review and Customize](#step-3-review-and-customize)
  - [Step 4: Play Your Quiz](#step-4-play-your-quiz)
- [Tips & Best Practices](#tips--best-practices)
  - [Writing Effective Topics](#writing-effective-topics)
  - [Using CSV Upload Effectively](#using-csv-upload-effectively)
  - [Question Design Best Practices](#question-design-best-practices)
  - [Optimizing Question Count](#optimizing-question-count)
- [Troubleshooting](#troubleshooting)
  - [AI Generation Issues](#ai-generation-issues)
  - [CSV Upload Issues](#csv-upload-issues)
  - [General Issues](#general-issues)
- [Frequently Asked Questions](#frequently-asked-questions)
- [Additional Resources](#additional-resources)
  - [Official Kahoot Resources](#official-kahoot-resources)
  - [Application Documentation](#application-documentation)
- [Need Help?](#need-help)
- [Example Workflow](#example-workflow)

---

## Introduction

Welcome to the Kahoot Quiz Generator! This application helps educators, trainers, and quiz creators create engaging multiple-choice quizzes for [Kahoot](https://kahoot.com/). The application offers two powerful modes:

1. **🤖 AI Generation**: Automatically generate quiz questions using AI based on any topic
2. **📄 CSV Upload**: Convert your existing questions from CSV format into Kahoot-ready quizzes

Both modes produce ready-to-upload Excel files that conform to Kahoot's official quiz template format.

## Who Is This For?

- Educators creating classroom quizzes
- Corporate trainers developing training assessments
- Content creators building engaging educational content
- Anyone with existing quiz questions who wants to convert them to Kahoot format
- Anyone who wants to quickly create Kahoot quizzes without manual formatting

## Prerequisites

Before you can use the application, you'll need:

1. Access to the running application (see [RUN.md](RUN.md) for setup instructions)
2. A web browser (Chrome, Firefox, Safari, or Edge)
3. A Kahoot account to upload your generated quiz (free accounts available at [kahoot.com](https://kahoot.com/))

## Quick Start Guide

### Step 1: Access the Application

Open your web browser and navigate to:

```url
http://localhost:8080
```

You should see the Kahoot Quiz Generator with two tabs at the top:

- **AI Generation** - Generate questions from a topic using AI
- **CSV Upload** - Convert your existing CSV quiz file to Kahoot format

Choose the mode that fits your needs and follow the instructions below.

---

## Mode 1: AI Generation

Use this mode when you want the AI to create quiz questions for you based on a topic.

### Step 1: Enter Your Quiz Topic

In the **Quiz Topic** field, provide a clear description or essential keywords for your quiz. The AI will use this to generate relevant questions.

**Examples of good topics:**

- "Solar System and planets"
- "World War II major events and battles"
- "JavaScript ES6 features and syntax"
- "Human anatomy - cardiovascular system"
- "Basic algebra equations and problem solving"

**Tips for effective topics:**

- Be specific rather than generic (e.g., "Photosynthesis in plants" vs. "Science")
- Include key concepts you want covered
- Use descriptive phrases rather than single words
- Mention the difficulty level if needed (e.g., "Introduction to Python for beginners")

### Step 2: Choose Number of Questions

Select how many questions you want (between 1 and 20).

**Recommended question counts:**

- **Quick review**: 5-10 questions
- **Standard quiz**: 10-15 questions
- **Comprehensive assessment**: 15-20 questions

### Step 3: Generate Your Quiz

Click the **Generate Quiz** button. The application will:

1. Send your topic to the AI
2. Generate unique questions with 4 multiple-choice answers each
3. Create an Excel file formatted for Kahoot
4. Automatically download the file to your computer

The file will be named `kahoot-quiz.xlsx`.

---

## Mode 2: CSV Upload

Use this mode when you already have quiz questions in a spreadsheet or CSV file.

### Step 1: Prepare Your CSV File

Your CSV file must include the following columns (column names are case-insensitive):

**Required Columns:**

- **Question** (or "q", "question_text") - The question text
- **Option 1** (or "option1", "answer 1", "choice 1", "a") - First answer choice
- **Option 2** (or "option2", "answer 2", "choice 2", "b") - Second answer choice
- **Option 3** (or "option3", "answer 3", "choice 3", "c") - Third answer choice
- **Option 4** (or "option4", "answer 4", "choice 4", "d") - Fourth answer choice
- **Correct Answer** (or "correct", "answer", "solution") - The text of the correct answer (must exactly match one of the four options)

**Optional Column:**

- **Time Limit** (or "time", "seconds", "duration") - Time limit in seconds (5-240). If not provided, defaults to 20 seconds.

**Example CSV Format:**

```csv
Question,Option 1,Option 2,Option 3,Option 4,Correct Answer,Time Limit
What is 2+2?,3,4,5,6,4,20
What is the capital of France?,London,Paris,Berlin,Madrid,Paris,30
Who wrote Romeo and Juliet?,Charles Dickens,William Shakespeare,Jane Austen,Mark Twain,William Shakespeare,25
```

**Important CSV Requirements:**

- Maximum 100 questions per file
- Maximum file size: 5MB
- File must have `.csv` extension
- Correct answer must **exactly match** one of the four options (case-sensitive)
- All questions must have exactly 4 answer options
- No empty questions or answer options

### Step 2: Upload Your CSV File

1. Click the **"Choose File"** or **"Browse"** button
2. Select your CSV file from your computer
3. Click the **"Upload CSV"** button

The application will parse your CSV file, detect column names, validate all questions and answers, and display a preview with any errors highlighted.

### Step 3: Review the Preview

After uploading, you'll see a table preview of your questions. The preview shows:

- ✅ Green indicators for valid questions
- ❌ Red highlighting for rows with errors
- 📊 Summary statistics (e.g., "18 of 20 questions valid")
- 📝 Detailed error messages explaining what needs to be fixed

**Common Validation Errors:**

- "Correct answer must exactly match one of the four options" - Check spelling and capitalization
- "Option X is empty" - One or more answer choices are missing
- "Time limit must be between 5 and 240 seconds" - Invalid time limit value
- "Question text is empty" - Missing question text

If there are errors, fix your CSV file and upload it again.

### Step 4: Configure Conversion Options

Once your CSV is valid (no errors), you'll see conversion options:

**Available Options:**

- **☑️ Shuffle question order** (default: enabled)
  - Randomizes the order of questions in the output
  - Useful for creating multiple quiz versions

- **☑️ Randomize answer positions** (default: enabled)
  - Shuffles the position of the correct answer for each question
  - Prevents answer pattern memorization

- **⏱️ Default time limit** (default: 20 seconds)
  - Applied to questions that don't have a time limit in the CSV
  - Valid range: 5-240 seconds

### Step 5: Generate Kahoot Quiz

1. Configure your shuffling and time limit preferences
2. Click the **"Generate Kahoot Quiz"** button
3. The Excel file will automatically download

The file will be named `kahoot-quiz-from-csv.xlsx`.

---

## Understanding Your Output

### What's in the Excel File?

The generated Excel file contains:

- Question text for each quiz question
- 4 answer choices per question (labeled Answer 1, Answer 2, Answer 3, Answer 4)
- Correct answer indicator showing which answer is correct
- Time limit for each question (default: 20 seconds)

### File Format

The Excel file follows [Kahoot's official quiz spreadsheet template format](https://kahoot.com/library/quiz-spreadsheet-template/), which means it's ready to upload directly to Kahoot without any manual formatting.

**Important:** The file structure includes:

- Row 8 onwards: Your quiz questions
- Column B: Question text
- Columns C-F: Answer choices 1-4
- Column G: Time limit (in seconds)
- Column H: Correct answer number (1, 2, 3, or 4)

---

## Uploading to Kahoot

Once you have your Excel file (from either AI Generation or CSV Upload), follow these steps to upload it to Kahoot.

### Step 1: Log into Kahoot

1. Go to [kahoot.com](https://kahoot.com/)
2. Click **Sign in** (or create a free account if you don't have one)
3. Navigate to your dashboard

### Step 2: Import Your Quiz

1. Click **"Create"** or **"Library"** in the top navigation
2. Look for **"Import spreadsheet"** or **"Upload from file"** option
3. Select your downloaded Excel file:
   - `kahoot-quiz.xlsx` (from AI Generation)
   - `kahoot-quiz-from-csv.xlsx` (from CSV Upload)
4. Click **"Upload"** or **"Import"**

The application will process your file and create a new Kahoot quiz.

### Step 3: Review and Customize

1. Review the imported questions
2. Verify that questions and answers are correct
3. Customize question settings if needed (adjust time limits, points, etc.)
4. Add images or videos if desired (optional)
5. Save your quiz

### Step 4: Play Your Quiz

Your quiz is now ready to play with students or participants!

---

## Tips & Best Practices

### Writing Effective Topics

✅ **DO:**

- Be specific: "French Revolution causes and outcomes"
- Include context: "State capitals of the United States"
- Mention difficulty: "Advanced calculus - derivatives"

❌ **DON'T:**

- Be too vague: "History"
- Use single words: "Math"
- Mix unrelated topics: "Science and geography and history"

### Using CSV Upload Effectively

1. **Prepare your CSV carefully**
   - Double-check spelling and capitalization in "Correct Answer" column
   - Remove extra spaces or special characters
   - Test with a small file (5-10 questions) first

2. **Use the preview feature**
   - Always review the preview before generating
   - Fix any validation errors shown in red
   - Verify question order and content

3. **Leverage shuffling options**
   - Enable "Shuffle question order" to create multiple quiz versions
   - Enable "Randomize answer positions" to prevent pattern memorization
   - Disable shuffling if you want questions in a specific order

4. **Set appropriate time limits**
   - Reading questions: 15-20 seconds
   - Math problems: 30-45 seconds
   - Complex analysis: 45-60 seconds
   - Adjust per-question in CSV for more control

### Question Design Best Practices

1. **Keep questions clear and concise**
   - Avoid overly complex sentence structures
   - Use simple, direct language
   - Aim for questions under 100 characters when possible

2. **Make distractors plausible**
   - Incorrect answers should be believable
   - Avoid obviously wrong choices
   - Use common misconceptions as incorrect answers

3. **Vary difficulty levels**
   - Mix easy, medium, and hard questions (70% medium, 20% easy, 10% hard)
   - Build confidence with easier questions first
   - End with challenging questions to maintain engagement

4. **Test one concept per question**
   - Don't combine multiple topics in one question
   - Keep focus narrow and specific

### Optimizing Question Count

- **Short attention spans?** Use 5-10 questions
- **Comprehensive testing?** Use 15-20 questions
- **Regular practice?** Use 10-12 questions
- **Competition/game?** Use 8-10 questions (keeps energy high)

---

## Troubleshooting

### AI Generation Issues

#### Issue: "Failed to generate quiz"

**Possible causes:**

- No internet connection
- AI service is temporarily unavailable
- Invalid topic or parameters

**Solutions:**

1. Check your internet connection
2. Try again in a few moments
3. Try a different, more specific topic
4. Reduce the number of questions

#### Issue: Questions not relevant to topic

**Possible causes:**

- Topic too vague
- AI misunderstood the context

**Solutions:**

- Regenerate with a more specific topic description
- Add more keywords to clarify intent
- Try rephrasing the topic

#### Issue: Duplicate questions

**Solutions:**

- Regenerate the quiz (AI generates new questions each time)
- Edit questions directly in Excel before uploading
- Modify questions after importing to Kahoot

### CSV Upload Issues

#### Issue: "File must be a CSV file with .csv extension"

**Solution:** Ensure your file is saved as `.csv` format, not `.xlsx` or other formats.

#### Issue: "File size exceeds maximum allowed size of 5 MB"

**Solution:** Reduce the number of questions or remove unnecessary columns from your CSV file.

#### Issue: "Correct answer must exactly match one of the four options"

**Cause:** The text in the "Correct Answer" column doesn't exactly match any of the four options.

**Solutions:**

1. Check for spelling errors
2. Verify capitalization matches exactly (case-sensitive)
3. Remove extra spaces before or after the answer text
4. Ensure you're using the exact same text

**Example:**

```csv
❌ Wrong: "Paris" vs "paris" (case mismatch)
❌ Wrong: "Paris" vs " Paris" (extra space)
✅ Correct: "Paris" matches "Paris" exactly
```

#### Issue: "Question column not found" or "Option X column not found"

**Solution:** Ensure your CSV has all required columns. The application accepts various column names (case-insensitive):

- Question: "Question", "q", "question_text"
- Options: "Option 1", "option1", "answer 1", "choice 1", "a"
- Correct Answer: "Correct Answer", "correct", "answer", "solution"

#### Issue: "Maximum 100 questions allowed"

**Solution:** Split your quiz into multiple CSV files, each with 100 or fewer questions.

### General Issues

#### Issue: Download doesn't start

**Possible causes:**

- Browser blocking downloads
- Application not running

**Solutions:**

- Check browser download settings
- Look for download notification in browser
- Verify application is running at <http://localhost:8080>

#### Issue: Downloaded file won't open

**Possible causes:**

- File download was interrupted
- Excel/spreadsheet software not installed

**Solutions:**

1. Try downloading the file again
2. Ensure you have Microsoft Excel, Google Sheets, or compatible software
3. Right-click the file and choose "Open with" to select your spreadsheet application

#### Issue: Kahoot rejects the Excel file

**Possible causes:**

- File format incompatibility
- Questions don't meet Kahoot's requirements

**Solutions:**

1. Download a fresh copy of the quiz file
2. Verify the file is in `.xlsx` format
3. Check that questions have exactly 4 answer choices
4. Ensure each question has exactly one correct answer
5. Open the file in Excel/Sheets to verify it looks correct before uploading

---

## Frequently Asked Questions

### Which mode should I use?

**Use AI Generation when:**

- You don't have existing questions
- You want AI to create questions for you
- You need questions on a specific topic quickly
- You're exploring new subject areas

**Use CSV Upload when:**

- You already have questions in a spreadsheet
- You're converting questions from another format
- You want full control over question wording
- You have a large question bank to import

### How many questions can I create?

- **AI Generation**: 1-20 questions per quiz
- **CSV Upload**: Up to 100 questions per file

For longer quizzes, generate multiple files and combine them in Kahoot.

### Can I edit questions after generation?

Yes! You can:

1. Open the Excel file in Microsoft Excel, Google Sheets, or similar
2. Edit questions, answers, or settings directly in the spreadsheet
3. Save the file
4. Upload the modified file to Kahoot

The Excel file follows Kahoot's template format, so you can freely modify it. You can also mix AI-generated questions with your own.

### How accurate are the generated questions?

The AI generates questions based on its training data. Always review questions for:

- Factual accuracy
- Age-appropriateness
- Clarity and grammar
- Alignment with your learning objectives

### Can I generate quizzes in languages other than English?

Yes! Provide your topic in any language, and the AI will generate questions in that language.

### How long does generation take?

Typically 10-30 seconds depending on:

- Number of questions requested
- Complexity of the topic
- AI provider response time

### Can I use the same topic multiple times?

Yes! Each generation creates unique questions, so you can generate multiple quizzes on the same topic for different classes or variations.

### What happens to my CSV data?

Your CSV file:

- ✅ Is processed in real-time when you upload
- ✅ Is validated and previewed in your browser
- ❌ Is NOT permanently stored on the server
- ❌ Is NOT shared or used for any other purpose

Once you close your browser, the data is gone.

### Can I use the same CSV file multiple times?

Yes! You can:

- Upload the same CSV with different shuffle settings
- Generate multiple versions of the same quiz
- Keep your CSV as a master question bank

### How much does it cost?

**AI Generation:**

- Requires an API key for the AI service (OpenAI or Groq Cloud)
- AI service may have costs depending on usage
- Check with your AI provider for pricing

**CSV Upload:**

- Completely free! No API key required
- No limits on uploads (within the 100 question/5MB constraints)

**Both modes require:**

- A Kahoot account (free basic accounts available)

### Can I mix AI-generated and CSV questions?

Not directly in the application, but you can:

1. Generate AI questions and download the Excel file
2. Upload CSV questions and download that Excel file
3. Open both files in Excel/Sheets
4. Copy/paste questions between files to create a combined quiz
5. Upload the combined file to Kahoot

### Is my data saved?

No. The application:

- ✅ Processes requests in real-time
- ✅ Generates Excel files on-demand
- ❌ Does not store quiz questions or topics
- ❌ Does not retain any personal data
- ❌ Does not track your uploads

---

## Additional Resources

### Official Kahoot Resources

- **Kahoot Homepage**: [https://kahoot.com/](https://kahoot.com/)
- **Kahoot Quiz Template**: [https://kahoot.com/library/quiz-spreadsheet-template/](https://kahoot.com/library/quiz-spreadsheet-template/)
- **Kahoot Help Center**: [https://support.kahoot.com/](https://support.kahoot.com/)
- **How to Create a Kahoot**: [https://support.kahoot.com/hc/en-us/articles/115002958768](https://support.kahoot.com/hc/en-us/articles/115002958768)
- **Import Questions Guide**: [https://support.kahoot.com/hc/en-us/articles/115002303908](https://support.kahoot.com/hc/en-us/articles/115002303908)

### Application Documentation

- **[BUILD.md](BUILD.md)** - How to build the application
- **[RUN.md](RUN.md)** - How to configure and run the application
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Technical architecture details
- **[DEPLOY.md](DEPLOY.md)** - Deployment instructions

## Need Help?

If you encounter issues not covered in this guide:

1. Check the [RUN.md](RUN.md) troubleshooting section
2. Review application logs for error messages
3. Verify your API key configuration in `config/creds.yml`
4. Open an issue on the [GitHub repository](https://github.com/pacphi/kahoot-quiz-generator/issues)

## Example Workflow

Here's a complete example from start to finish:

1. Start the application (see [RUN.md](RUN.md))
2. Open browser to <http://localhost:8080>
3. Enter topic: "Ancient Egypt civilization and pharaohs"
4. Set questions: 10
5. Click Generate Quiz
6. Download `kahoot-quiz.xlsx` to your computer
7. Log into Kahoot at <https://kahoot.com>
8. Click Create → Import spreadsheet
9. Upload `kahoot-quiz.xlsx`
10. Review and customize questions
11. Publish your Kahoot quiz
12. Share with students or participants

Enjoy creating engaging quizzes with the Kahoot Quiz Generator!
