import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Alert, AlertDescription } from './ui/alert';
import { Button } from './ui/button';
import { Input, Label } from './ui/input';
import { Download, Loader2, Settings, CheckCircle2 } from 'lucide-react';

const ConversionOptions = ({ questions, onConvert }) => {
  const [options, setOptions] = useState({
    shuffleQuestions: true,
    randomizeAnswers: true,
    defaultTimeLimit: 20,
  });
  const [isConverting, setIsConverting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleOptionChange = (field, value) => {
    setOptions(prev => ({
      ...prev,
      [field]: value,
    }));
    setError(null);
  };

  const validateOptions = () => {
    const { defaultTimeLimit } = options;

    if (defaultTimeLimit < 5) {
      setError('Time limit must be at least 5 seconds');
      return false;
    }

    if (defaultTimeLimit > 240) {
      setError('Time limit cannot exceed 240 seconds');
      return false;
    }

    if (!Number.isInteger(Number(defaultTimeLimit))) {
      setError('Time limit must be a whole number');
      return false;
    }

    return true;
  };

  const downloadFile = (blob, filename) => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  };

  const handleConvert = async () => {
    if (!validateOptions()) {
      return;
    }

    setIsConverting(true);
    setError(null);
    setSuccess(false);

    try {
      const conversionRequest = {
        questions: questions.map(q => ({
          question: q.question,
          option1: q.option1,
          option2: q.option2,
          option3: q.option3,
          option4: q.option4,
          correct: q.correct,
          timeLimit: q.timeLimit || options.defaultTimeLimit,
        })),
        shuffleQuestions: options.shuffleQuestions,
        randomizeAnswers: options.randomizeAnswers,
        defaultTimeLimit: options.defaultTimeLimit,
      };

      const response = await fetch('/api/quiz/convert', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(conversionRequest),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Conversion failed' }));
        throw new Error(errorData.message || `Conversion failed with status ${response.status}`);
      }

      // Get the blob from the response
      const blob = await response.blob();

      // Extract filename from Content-Disposition header or use default
      const contentDisposition = response.headers.get('Content-Disposition');
      let filename = 'kahoot_quiz.xlsx';

      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
        if (filenameMatch && filenameMatch[1]) {
          filename = filenameMatch[1].replace(/['"]/g, '');
        }
      }

      // Trigger download
      downloadFile(blob, filename);

      setSuccess(true);
      setError(null);

      // Call optional callback
      if (onConvert) {
        onConvert({ success: true, filename });
      }

      // Reset success message after 5 seconds
      setTimeout(() => setSuccess(false), 5000);
    } catch (err) {
      setError(err.message || 'Failed to convert CSV to Kahoot format. Please try again.');
      setSuccess(false);

      if (onConvert) {
        onConvert({ success: false, error: err.message });
      }
    } finally {
      setIsConverting(false);
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Settings className="w-6 h-6" />
          Conversion Options
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        {error && (
          <Alert variant="destructive">
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {success && (
          <Alert className="bg-green-50 text-green-800 border-green-200">
            <AlertDescription className="flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5" />
              Quiz generated successfully! Download should start automatically.
            </AlertDescription>
          </Alert>
        )}

        <div className="space-y-4">
          {/* Shuffle Questions */}
          <div className="flex items-center space-x-3">
            <input
              type="checkbox"
              id="shuffleQuestions"
              checked={options.shuffleQuestions}
              onChange={(e) => handleOptionChange('shuffleQuestions', e.target.checked)}
              disabled={isConverting}
              className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 focus:ring-2 cursor-pointer disabled:opacity-50"
            />
            <Label htmlFor="shuffleQuestions" className="cursor-pointer">
              Shuffle question order
            </Label>
          </div>

          {/* Randomize Answers */}
          <div className="flex items-center space-x-3">
            <input
              type="checkbox"
              id="randomizeAnswers"
              checked={options.randomizeAnswers}
              onChange={(e) => handleOptionChange('randomizeAnswers', e.target.checked)}
              disabled={isConverting}
              className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 focus:ring-2 cursor-pointer disabled:opacity-50"
            />
            <Label htmlFor="randomizeAnswers" className="cursor-pointer">
              Randomize answer positions
            </Label>
          </div>

          {/* Default Time Limit */}
          <div className="space-y-2">
            <Label htmlFor="defaultTimeLimit">
              Default time limit (seconds)
            </Label>
            <Input
              type="number"
              id="defaultTimeLimit"
              min="5"
              max="240"
              value={options.defaultTimeLimit}
              onChange={(e) => handleOptionChange('defaultTimeLimit', parseInt(e.target.value, 10) || 20)}
              disabled={isConverting}
              className="w-full max-w-xs"
            />
            <p className="text-sm text-gray-500">
              Applied to questions without a specified time limit (5-240 seconds)
            </p>
          </div>
        </div>

        <div className="pt-4 border-t border-gray-200">
          <Button
            onClick={handleConvert}
            disabled={isConverting || !questions || questions.length === 0}
            className="w-full"
            size="lg"
          >
            {isConverting ? (
              <>
                <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                Generating Kahoot Quiz...
              </>
            ) : (
              <>
                <Download className="w-5 h-5 mr-2" />
                Generate Kahoot Quiz
              </>
            )}
          </Button>

          <p className="mt-3 text-sm text-center text-gray-500">
            {questions?.length || 0} questions will be converted to Kahoot format (.xlsx)
          </p>
        </div>
      </CardContent>
    </Card>
  );
};

export default ConversionOptions;
