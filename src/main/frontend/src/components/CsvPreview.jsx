import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Alert, AlertDescription } from './ui/alert';
import ConversionOptions from './ConversionOptions';
import { CheckCircle2, XCircle, AlertCircle, ChevronRight, ChevronDown, Info } from 'lucide-react';

const CsvPreview = ({ previewData }) => {
  const [expandedRow, setExpandedRow] = useState(null);

  if (!previewData) return null;

  const { questions = [], validQuestions = 0, totalQuestions = 0, errors = [], columnMapping = {} } = previewData;
  const hasErrors = validQuestions < totalQuestions;
  const allValid = validQuestions === totalQuestions && totalQuestions > 0;

  // Helper functions to extract data from question object
  const getOption = (question, index) => {
    if (!question.choices || !question.choices[index]) return null;
    return question.choices[index].answerText;
  };

  const getCorrectAnswer = (question) => {
    if (!question.choices) return null;
    const correctChoice = question.choices.find(c => c.isCorrect);
    return correctChoice ? correctChoice.answerText : null;
  };

  const getRowClassName = (question, idx) => {
    const isExpanded = expandedRow === idx;
    const hasError = question.errors && question.errors.length > 0;

    if (hasError) {
      return 'bg-red-50 cursor-pointer';
    }
    if (isExpanded) {
      return 'bg-blue-50 cursor-pointer';
    }
    return 'hover:bg-gray-50 cursor-pointer';
  };

  const toggleRow = (idx) => {
    setExpandedRow(expandedRow === idx ? null : idx);
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span className="flex items-center gap-2">
              <AlertCircle className="w-6 h-6" />
              CSV Preview
            </span>
            <span className="text-sm font-normal">
              {allValid ? (
                <span className="flex items-center gap-2 text-green-600">
                  <CheckCircle2 className="w-5 h-5" />
                  {validQuestions} of {totalQuestions} questions valid
                </span>
              ) : (
                <span className="flex items-center gap-2 text-red-600">
                  <XCircle className="w-5 h-5" />
                  {validQuestions} of {totalQuestions} questions valid
                </span>
              )}
            </span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          {Object.keys(columnMapping).length > 0 && (
            <Alert className="mb-4 bg-blue-50 border-blue-200">
              <Info className="h-4 w-4 text-blue-600" />
              <AlertDescription>
                <div className="text-sm text-blue-900">
                  <span className="font-semibold">Detected Columns: </span>
                  {Object.entries(columnMapping).map(([key, value], idx, arr) => (
                    <span key={key}>
                      <span className="font-medium">{key}</span>
                      <span className="mx-1">→</span>
                      <span className="italic">'{value}'</span>
                      {idx < arr.length - 1 && <span className="mx-2">|</span>}
                    </span>
                  ))}
                </div>
              </AlertDescription>
            </Alert>
          )}

          {errors.length > 0 && (
            <Alert variant="destructive" className="mb-4">
              <AlertDescription>
                <div className="font-semibold mb-1">File Validation Errors:</div>
                <ul className="list-disc list-inside space-y-1">
                  {errors.map((error, idx) => (
                    <li key={idx} className="text-sm">{error}</li>
                  ))}
                </ul>
              </AlertDescription>
            </Alert>
          )}

          {questions.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full border-collapse">
                <thead>
                  <tr className="bg-gray-100 border-b-2 border-gray-300">
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">#</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Question</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Option 1</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Option 2</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Option 3</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Option 4</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Correct</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Time (s)</th>
                  </tr>
                </thead>
                <tbody>
                  {questions.map((question, idx) => {
                    const isExpanded = expandedRow === idx;
                    const option1 = getOption(question, 0);
                    const option2 = getOption(question, 1);
                    const option3 = getOption(question, 2);
                    const option4 = getOption(question, 3);
                    const correctAnswer = getCorrectAnswer(question);

                    return (
                    <React.Fragment key={idx}>
                      <tr
                        className={`border-b border-gray-200 ${getRowClassName(question, idx)}`}
                        onClick={() => toggleRow(idx)}
                      >
                        <td className="px-4 py-3 text-sm font-medium text-gray-900">
                          <div className="flex items-center gap-2">
                            {isExpanded ? (
                              <ChevronDown className="w-4 h-4 text-blue-600" />
                            ) : (
                              <ChevronRight className="w-4 h-4 text-gray-400" />
                            )}
                            {idx + 1}
                          </div>
                        </td>
                        <td className={`px-4 py-3 text-sm text-gray-900 ${isExpanded ? 'whitespace-pre-wrap break-words' : 'max-w-xs truncate'}`} title={!isExpanded ? question.question : undefined}>
                          {question.question || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className={`px-4 py-3 text-sm text-gray-700 ${isExpanded ? 'whitespace-pre-wrap break-words' : 'max-w-[150px] truncate'}`} title={!isExpanded ? option1 : undefined}>
                          {option1 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className={`px-4 py-3 text-sm text-gray-700 ${isExpanded ? 'whitespace-pre-wrap break-words' : 'max-w-[150px] truncate'}`} title={!isExpanded ? option2 : undefined}>
                          {option2 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className={`px-4 py-3 text-sm text-gray-700 ${isExpanded ? 'whitespace-pre-wrap break-words' : 'max-w-[150px] truncate'}`} title={!isExpanded ? option3 : undefined}>
                          {option3 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className={`px-4 py-3 text-sm text-gray-700 ${isExpanded ? 'whitespace-pre-wrap break-words' : 'max-w-[150px] truncate'}`} title={!isExpanded ? option4 : undefined}>
                          {option4 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className={`px-4 py-3 text-sm font-medium text-gray-900 ${isExpanded ? 'whitespace-pre-wrap break-words' : 'max-w-[150px] truncate'}`} title={!isExpanded ? correctAnswer : undefined}>
                          {correctAnswer || <span className="text-gray-400 italic">-</span>}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-700">
                          {question.timeLimit || <span className="text-gray-400 italic">-</span>}
                        </td>
                      </tr>
                      {question.errors && question.errors.length > 0 && (
                        <tr className="bg-red-50">
                          <td colSpan="8" className="px-4 py-2">
                            <div className="flex items-start gap-2 text-sm text-red-700">
                              <XCircle className="w-4 h-4 mt-0.5 flex-shrink-0" />
                              <div>
                                <span className="font-semibold">Validation errors:</span>
                                <ul className="list-disc list-inside mt-1 space-y-0.5">
                                  {question.errors.map((error, errorIdx) => (
                                    <li key={errorIdx}>{error}</li>
                                  ))}
                                </ul>
                              </div>
                            </div>
                          </td>
                        </tr>
                      )}
                    </React.Fragment>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <Alert>
              <AlertDescription>
                No questions found in the CSV file. Please check the file format.
              </AlertDescription>
            </Alert>
          )}
        </CardContent>
      </Card>

      {allValid && <ConversionOptions questions={questions} />}
    </div>
  );
};

export default CsvPreview;
