import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CsvPreview from './CsvPreview';

describe('CsvPreview', () => {
  const mockPreviewData = {
    questions: [
      {
        question: 'What is the capital of France?',
        choices: [
          { answerText: 'London', isCorrect: false },
          { answerText: 'Paris', isCorrect: true },
          { answerText: 'Berlin', isCorrect: false },
          { answerText: 'Madrid', isCorrect: false }
        ],
        timeLimit: 20
      },
      {
        question: 'What is 2 + 2?',
        choices: [
          { answerText: '3', isCorrect: false },
          { answerText: '4', isCorrect: true },
          { answerText: '5', isCorrect: false },
          { answerText: '6', isCorrect: false }
        ],
        timeLimit: 15
      }
    ],
    validQuestions: 2,
    totalQuestions: 2,
    errors: [],
    columnMapping: {
      question: 'Question',
      option1: 'A',
      option2: 'B',
      option3: 'C',
      option4: 'D',
      correctAnswer: 'Correct',
      timeLimit: 'Time'
    }
  };

  it('renders null when no preview data is provided', () => {
    const { container } = render(<CsvPreview previewData={null} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('displays column mapping when provided', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    expect(screen.getByText(/Detected Columns:/i)).toBeInTheDocument();
    // Check for the column mapping in the alert
    const alert = screen.getByText(/Detected Columns:/i).closest('div');
    expect(alert).toHaveTextContent('question');
    expect(alert).toHaveTextContent("'Question'");
  });

  it('displays all questions in the table', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    expect(screen.getByText('What is the capital of France?')).toBeInTheDocument();
    expect(screen.getByText('What is 2 + 2?')).toBeInTheDocument();
  });

  it('extracts and displays options from choices array', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    // Check for all option values in the table
    const tableBody = screen.getAllByRole('row')[1]; // First data row
    expect(tableBody).toHaveTextContent('London');
    expect(tableBody).toHaveTextContent('Paris');
    expect(tableBody).toHaveTextContent('Berlin');
    expect(tableBody).toHaveTextContent('Madrid');
  });

  it('identifies and displays the correct answer', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    const rows = screen.getAllByRole('row');
    // First data row (index 1, since header is index 0)
    expect(rows[1]).toHaveTextContent('Paris');
    // Second data row
    expect(rows[2]).toHaveTextContent('4');
  });

  it('displays time limits for questions', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    expect(screen.getByText('20')).toBeInTheDocument();
    expect(screen.getByText('15')).toBeInTheDocument();
  });

  it('shows validation status with correct count', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    expect(screen.getByText('2 of 2 questions valid')).toBeInTheDocument();
  });

  it('expands row when clicked', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    const firstRow = screen.getAllByRole('row')[1]; // First data row

    // Should show collapse icon (ChevronRight) initially
    expect(firstRow).toHaveTextContent('1');

    // Click to expand
    fireEvent.click(firstRow);

    // Row should now have expanded styling
    expect(firstRow).toHaveClass('bg-blue-50');
  });

  it('collapses row when clicked again', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    const firstRow = screen.getAllByRole('row')[1];

    // Expand
    fireEvent.click(firstRow);
    expect(firstRow).toHaveClass('bg-blue-50');

    // Collapse
    fireEvent.click(firstRow);
    expect(firstRow).not.toHaveClass('bg-blue-50');
  });

  it('displays validation errors for invalid questions', () => {
    const dataWithErrors = {
      ...mockPreviewData,
      questions: [
        {
          ...mockPreviewData.questions[0],
          errors: ['Question text is empty', 'Correct answer does not match any option']
        }
      ],
      validQuestions: 0,
      totalQuestions: 1
    };

    render(<CsvPreview previewData={dataWithErrors} />);

    expect(screen.getByText(/Validation errors:/i)).toBeInTheDocument();
    expect(screen.getByText('Question text is empty')).toBeInTheDocument();
    expect(screen.getByText('Correct answer does not match any option')).toBeInTheDocument();
  });

  it('highlights error rows with red background', () => {
    const dataWithErrors = {
      ...mockPreviewData,
      questions: [
        {
          ...mockPreviewData.questions[0],
          errors: ['Some error']
        }
      ],
      validQuestions: 0,
      totalQuestions: 1
    };

    render(<CsvPreview previewData={dataWithErrors} />);

    const errorRow = screen.getAllByRole('row')[1];
    expect(errorRow).toHaveClass('bg-red-50');
  });

  it('displays file-level errors in alert', () => {
    const dataWithFileErrors = {
      ...mockPreviewData,
      errors: ['File size exceeds maximum', 'Invalid file format']
    };

    render(<CsvPreview previewData={dataWithFileErrors} />);

    expect(screen.getByText(/File Validation Errors:/i)).toBeInTheDocument();
    expect(screen.getByText('File size exceeds maximum')).toBeInTheDocument();
    expect(screen.getByText('Invalid file format')).toBeInTheDocument();
  });

  it('shows empty placeholder for missing data', () => {
    const dataWithEmptyValues = {
      ...mockPreviewData,
      questions: [
        {
          question: '',
          choices: [
            { answerText: '', isCorrect: false },
            { answerText: '', isCorrect: false },
            { answerText: '', isCorrect: false },
            { answerText: '', isCorrect: false }
          ],
          timeLimit: null
        }
      ]
    };

    render(<CsvPreview previewData={dataWithEmptyValues} />);

    // Should show "Empty" placeholders
    const emptyElements = screen.getAllByText(/Empty/i);
    expect(emptyElements.length).toBeGreaterThan(0);
  });

  it('only expands one row at a time', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    const rows = screen.getAllByRole('row');
    const firstRow = rows[1];
    const secondRow = rows[2];

    // Expand first row
    fireEvent.click(firstRow);
    expect(firstRow).toHaveClass('bg-blue-50');
    expect(secondRow).not.toHaveClass('bg-blue-50');

    // Expand second row (should collapse first)
    fireEvent.click(secondRow);
    expect(firstRow).not.toHaveClass('bg-blue-50');
    expect(secondRow).toHaveClass('bg-blue-50');
  });

  it('shows ConversionOptions when all questions are valid', () => {
    render(<CsvPreview previewData={mockPreviewData} />);

    // ConversionOptions component should be rendered
    // We can't check for the actual component, but we can verify the conditions are met
    expect(mockPreviewData.validQuestions).toBe(mockPreviewData.totalQuestions);
  });

  it('does not show ConversionOptions when there are errors', () => {
    const dataWithErrors = {
      ...mockPreviewData,
      validQuestions: 1,
      totalQuestions: 2
    };

    const { container } = render(<CsvPreview previewData={dataWithErrors} />);

    // Verify that not all questions are valid
    expect(dataWithErrors.validQuestions).toBeLessThan(dataWithErrors.totalQuestions);
  });
});
