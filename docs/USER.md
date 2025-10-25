# User Guide - Kahoot Quiz Generator

## Introduction

Welcome to the Kahoot Quiz Generator! This application helps educators, trainers, and quiz creators automatically generate engaging multiple-choice quizzes using AI. Simply provide a topic and the number of questions you want, and the application will create a ready-to-upload Excel file for [Kahoot](https://kahoot.com/).

## Who Is This For?

- **Educators** creating classroom quizzes
- **Corporate trainers** developing training assessments
- **Content creators** building engaging educational content
- **Anyone** who wants to quickly create Kahoot quizzes without manual question writing

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

You should see the "Generate Kahoot Quiz" interface.

### Step 2: Enter Your Quiz Topic

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

### Step 3: Choose Number of Questions

Select how many questions you want (between 1 and 20).

**Recommended question counts:**
- **Quick review**: 5-10 questions
- **Standard quiz**: 10-15 questions
- **Comprehensive assessment**: 15-20 questions

### Step 4: Generate Your Quiz

Click the **Generate Quiz** button. The application will:
1. Send your topic to the AI
2. Generate unique questions with 4 multiple-choice answers each
3. Create an Excel file formatted for Kahoot
4. Automatically download the file to your computer

The file will be named `kahoot-quiz.xlsx`.

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

## Uploading to Kahoot

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
