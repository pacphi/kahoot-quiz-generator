# User Guide - Kahoot Quiz Generator

## Introduction

Welcome to the Kahoot Quiz Generator! This application helps educators, trainers, and quiz creators create engaging multiple-choice quizzes for [Kahoot](https://kahoot.com/). The application offers **two powerful modes**:

1. **🤖 AI Generation**: Automatically generate quiz questions using AI based on any topic
2. **📄 CSV Upload**: Convert your existing questions from CSV format into Kahoot-ready quizzes

Both modes produce ready-to-upload Excel files that conform to Kahoot's official quiz template format.

## Who Is This For?

- **Educators** creating classroom quizzes
- **Corporate trainers** developing training assessments
- **Content creators** building engaging educational content
- **Anyone with existing quiz questions** who wants to convert them to Kahoot format
- **Anyone** who wants to quickly create Kahoot quizzes without manual formatting

## Prerequisites

Before you can use the application, you'll need:

1. **Access to the running application** (see [RUN.md](RUN.md) for setup instructions)
2. **A web browser** (Chrome, Firefox, Safari, or Edge)
3. **A Kahoot account** to upload your generated quiz (free accounts available at [kahoot.com](https://kahoot.com/))

## Quick Start Guide

### Step 1: Access the Application

Open your web browser and navigate to:
```
http://localhost:8080
```

You should see the Kahoot Quiz Generator with two tabs at the top:
- **AI Generation** - Generate questions from a topic using AI
- **CSV Upload** - Convert your existing CSV quiz file to Kahoot format

**[SCREENSHOT PLACEHOLDER: Main interface showing both tabs]**

Choose the mode that fits your needs and follow the instructions below.

---

## Mode 1: AI Generation

Use this mode when you want the AI to create quiz questions for you based on a topic.

**[SCREENSHOT PLACEHOLDER: AI Generation tab interface]**

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

**[SCREENSHOT PLACEHOLDER: CSV Upload tab interface]**

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
- ✅ Maximum 100 questions per file
- ✅ Maximum file size: 5MB
- ✅ File must have `.csv` extension
- ✅ Correct answer must **exactly match** one of the four options (case-sensitive)
- ✅ All questions must have exactly 4 answer options
- ✅ No empty questions or answer options

**[SCREENSHOT PLACEHOLDER: Sample CSV file in Excel/spreadsheet]**

### Step 2: Upload Your CSV File

1. Click the **"Choose File"** or **"Browse"** button
2. Select your CSV file from your computer
3. Click the **"Upload CSV"** button

The application will:
- Parse your CSV file
- Detect column names (even with different capitalization)
- Validate all questions and answers
- Display a preview with any errors highlighted

**[SCREENSHOT PLACEHOLDER: File upload interface with file selected]**

### Step 3: Review the Preview

After uploading, you'll see a table preview of your questions:

**[SCREENSHOT PLACEHOLDER: CSV preview table showing questions]**

The preview shows:
- ✅ **Green indicators** for valid questions
- ❌ **Red highlighting** for rows with errors
- 📊 **Summary statistics** (e.g., "18 of 20 questions valid")
- 📝 **Detailed error messages** explaining what needs to be fixed

**Common Validation Errors:**
- "Correct answer must exactly match one of the four options" - Check spelling and capitalization
- "Option X is empty" - One or more answer choices are missing
- "Time limit must be between 5 and 240 seconds" - Invalid time limit value
- "Question text is empty" - Missing question text

If there are errors, fix your CSV file and upload it again.

**[SCREENSHOT PLACEHOLDER: Preview showing validation errors highlighted in red]**

### Step 4: Configure Conversion Options

Once your CSV is valid (no errors), you'll see conversion options:

**[SCREENSHOT PLACEHOLDER: Conversion options panel]**

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

- **Question text** for each quiz question
- **4 answer choices** per question (labeled Answer 1, Answer 2, Answer 3, Answer 4)
- **Correct answer indicator** showing which answer is correct
- **Time limit** for each question (default: 20 seconds)

### File Format

The Excel file follows [Kahoot's official quiz spreadsheet template format](https://kahoot.com/library/quiz-spreadsheet-template/), which means it's ready to upload directly to Kahoot without any manual formatting.

**Important:** The file structure includes:
- Row 8 onwards: Your quiz questions
- Column B: Question text
- Columns C-F: Answer choices 1-4
- Column G: Time limit (in seconds)
- Column H: Correct answer number (1, 2, 3, or 4)

**[SCREENSHOT PLACEHOLDER: Successfully generated quiz showing download]**

---

## Uploading to Kahoot

Once you have your Excel file (from either AI Generation or CSV Upload), follow these steps to upload it to Kahoot.

**[SCREENSHOT PLACEHOLDER: Kahoot website homepage]**

### Step 1: Log into Kahoot

1. Go to [kahoot.com](https://kahoot.com/)
2. Click **Sign in** (or create a free account if you don't have one)
3. Navigate to your dashboard

### Step 2: Import Your Quiz

1. Click the **Create** button or **Library** section
2. Select **Import spreadsheet** or **Upload**
3. Choose the `kahoot-quiz.xlsx` file you downloaded
4. Kahoot will process the file and import your questions

### Step 3: Review and Customize

After import, you can:
- Review each question
- Add images or videos to questions
- Adjust time limits
- Modify answer choices
- Add question explanations

### Step 4: Publish Your Kahoot

1. Click **Done** or **Save**
2. Add a cover image and description (optional)
3. Set visibility (Public or Private)
4. Click **Publish** or **Save**

Your quiz is now ready to play!

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

### Optimizing Question Count

- **Short attention spans?** Use 5-10 questions
- **Comprehensive testing?** Use 15-20 questions
- **Regular practice?** Use 10-12 questions
- **Competition/game?** Use 8-10 questions (keeps energy high)

### Quality Control

After generating your quiz:
1. **Review all questions** for accuracy and clarity
2. **Check answer choices** ensure they're distinct and reasonable
3. **Verify correct answers** are indeed correct
4. **Test the quiz** with a small group before wider use

## Troubleshooting

### Issue: Download Doesn't Start

**Possible causes:**
- Browser blocking downloads
- Application not running

**Solutions:**
- Check browser download settings
- Look for download notification in browser
- Verify application is running at http://localhost:8080

### Issue: Questions Not Relevant to Topic

**Possible causes:**
- Topic too vague
- AI misunderstood the context

**Solutions:**
- Regenerate with a more specific topic description
- Add more keywords to clarify intent
- Try rephrasing the topic

### Issue: Kahoot Rejects the Excel File

**Possible causes:**
- File corrupted during download
- Excel version compatibility

**Solutions:**
- Re-download the file
- Try opening in Excel/LibreOffice to verify format
- Regenerate the quiz
- Use Kahoot's template as reference

### Issue: Duplicate Questions

**Solutions:**
- Regenerate the quiz (AI generates new questions each time)
- Edit questions directly in Excel before uploading
- Modify questions after importing to Kahoot

## Frequently Asked Questions

### Can I edit questions after generation?

**Yes!** You can:
- Edit the Excel file directly before uploading to Kahoot
- Modify questions after importing to Kahoot
- Mix AI-generated questions with your own

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

1. **Start the application** (see [RUN.md](RUN.md))
2. **Open browser** to http://localhost:8080
3. **Enter topic**: "Ancient Egypt civilization and pharaohs"
4. **Set questions**: 10
5. **Click Generate Quiz**
6. **Download** `kahoot-quiz.xlsx` to your computer
7. **Log into Kahoot** at kahoot.com
8. **Click Create → Import spreadsheet**
9. **Upload** `kahoot-quiz.xlsx`
10. **Review and customize** questions
11. **Publish** your Kahoot quiz
12. **Share** with students or participants

Enjoy creating engaging quizzes with the Kahoot Quiz Generator!
