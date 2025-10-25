import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Alert, AlertDescription } from './ui/alert';
import ConversionOptions from './ConversionOptions';
import { CheckCircle2, XCircle, AlertCircle } from 'lucide-react';

const CsvPreview = ({ previewData }) => {
  if (!previewData) return null;

  const { questions = [], validQuestions = 0, totalQuestions = 0, errors = [] } = previewData;
  const hasErrors = validQuestions < totalQuestions;
  const allValid = validQuestions === totalQuestions && totalQuestions > 0;

  const getRowClassName = (question) => {
    if (question.errors && question.errors.length > 0) {
      return 'bg-red-50';
    }
    return 'hover:bg-gray-50';
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
                  {questions.map((question, idx) => (
                    <React.Fragment key={idx}>
                      <tr className={`border-b border-gray-200 ${getRowClassName(question)}`}>
                        <td className="px-4 py-3 text-sm font-medium text-gray-900">
                          {idx + 1}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-900 max-w-xs truncate" title={question.question}>
                          {question.question || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-700">
                          {question.option1 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-700">
                          {question.option2 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-700">
                          {question.option3 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-700">
                          {question.option4 || <span className="text-gray-400 italic">Empty</span>}
                        </td>
                        <td className="px-4 py-3 text-sm font-medium text-gray-900">
                          {question.correct || <span className="text-gray-400 italic">-</span>}
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
                  ))}
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
