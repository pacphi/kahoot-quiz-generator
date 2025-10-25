## APPEND THIS TO USER.MD AFTER LINE 232 (after "Navigate to your dashboard"):

### Step 2: Import Your Quiz

**[SCREENSHOT PLACEHOLDER: Kahoot import spreadsheet option]**

1. Click **"Create"** or **"Library"** in the top navigation
2. Look for **"Import spreadsheet"** or **"Upload from file"** option
3. Select your downloaded Excel file:
   - `kahoot-quiz.xlsx` (from AI Generation)
   - `kahoot-quiz-from-csv.xlsx` (from CSV Upload)
4. Click **"Upload"** or **"Import"**

The application will process your file and create a new Kahoot quiz.

**[SCREENSHOT PLACEHOLDER: File upload dialog in Kahoot]**

### Step 3: Review and Customize

**[SCREENSHOT PLACEHOLDER: Imported quiz in Kahoot editor]**

1. Review the imported questions
2. Verify that questions and answers are correct
3. Customize question settings if needed (adjust time limits, points, etc.)
4. Add images or videos if desired (optional)
5. Save your quiz

### Step 4: Play Your Quiz!

**[SCREENSHOT PLACEHOLDER: Kahoot game in progress]**

Your quiz is now ready to play with students or participants!

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

#### Issue: Downloaded file won't open

**Possible causes:**
- File download was interrupted
- Excel/spreadsheet software not installed

**Solutions:**
1. Try downloading the file again
2. Ensure you have Microsoft Excel, Google Sheets, or compatible software
3. Right-click the file and choose "Open with" to select your spreadsheet application

#### Issue: Kahoot won't accept the uploaded file

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

## Tips for Best Results

### Using AI Generation Effectively

**Writing Effective Topics:**

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

### Question Design Best Practices (Both Modes)

1. **Keep questions clear and concise**
   - Avoid overly complex sentence structures
   - Use simple, direct language
   - Aim for questions under 100 characters when possible

2. **Make distractors plausible**
   - Incorrect answers should be believable
   - Avoid obviously wrong choices like "banana" for "What is 2+2?"
   - Use common misconceptions as incorrect answers

3. **Vary difficulty levels**
   - Mix easy, medium, and hard questions (70% medium, 20% easy, 10% hard)
   - Build confidence with easier questions first
   - End with challenging questions to maintain engagement

4. **Test one concept per question**
   - Don't combine multiple topics in one question
   - Keep focus narrow and specific
   - Example: Ask "What is photosynthesis?" not "What is photosynthesis and how does it relate to cellular respiration?"

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

**Note:** The Excel file follows Kahoot's template format, so you can freely modify it.

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

Enjoy creating engaging quizzes with the Kahoot Quiz Generator!
