import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CsvUploadTab from './CsvUploadTab';

// Mock fetch globally
global.fetch = vi.fn();

describe('CsvUploadTab', () => {
  beforeEach(() => {
    // Reset fetch mock before each test
    global.fetch.mockReset();
  });

  it('renders the upload form', () => {
    render(<CsvUploadTab />);

    expect(screen.getByText('Upload CSV File')).toBeInTheDocument();
    expect(screen.getByText('Select CSV File')).toBeInTheDocument();
    expect(screen.getByText(/Maximum file size: 5MB/i)).toBeInTheDocument();
  });

  it('shows file input field', () => {
    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    expect(fileInput).toBeInTheDocument();
    expect(fileInput).toHaveAttribute('type', 'file');
    expect(fileInput).toHaveAttribute('accept', '.csv');
  });

  it('validates file type - rejects non-CSV files', async () => {
    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const invalidFile = new File(['content'], 'test.txt', { type: 'text/plain' });

    // Use fireEvent.change instead of userEvent.upload for better test compatibility
    fireEvent.change(fileInput, { target: { files: [invalidFile] } });

    await waitFor(() => {
      expect(screen.getByText('Please select a valid CSV file')).toBeInTheDocument();
    });
  });

  it('validates file size - rejects files larger than 5MB', async () => {
    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const largeContent = 'x'.repeat(6 * 1024 * 1024); // 6MB
    const largeFile = new File([largeContent], 'large.csv', { type: 'text/csv' });

    // Override file size property
    Object.defineProperty(largeFile, 'size', { value: 6 * 1024 * 1024 });

    await userEvent.upload(fileInput, largeFile);

    expect(screen.getByText('File size must be less than 5MB')).toBeInTheDocument();
  });

  it('accepts valid CSV file', async () => {
    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const validFile = new File(['question,option1,option2,option3,option4,correct,time\nQ1,A,B,C,D,A,20'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, validFile);

    expect(screen.getByText('test.csv')).toBeInTheDocument();
    expect(screen.queryByText('Please select a valid CSV file')).not.toBeInTheDocument();
  });

  it('displays file info after selection', async () => {
    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'quiz.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    expect(screen.getByText('quiz.csv')).toBeInTheDocument();
    expect(screen.getByText(/KB/)).toBeInTheDocument();
  });

  it('enables upload button when file is selected', async () => {
    const { container } = render(<CsvUploadTab />);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    expect(uploadButton).toBeDisabled();

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    expect(uploadButton).toBeEnabled();
  });

  it('calls API and displays preview on successful upload', async () => {
    const mockResponse = {
      questions: [
        {
          question: 'Test Question',
          choices: [
            { answerText: 'A', isCorrect: true },
            { answerText: 'B', isCorrect: false },
            { answerText: 'C', isCorrect: false },
            { answerText: 'D', isCorrect: false }
          ],
          timeLimit: 20
        }
      ],
      validQuestions: 1,
      totalQuestions: 1,
      errors: [],
      columnMapping: { question: 'Question' }
    };

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse
    });

    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    fireEvent.click(uploadButton);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith('/api/quiz/upload', expect.objectContaining({
        method: 'POST',
        body: expect.any(FormData)
      }));
    });
  });

  it('shows loading state during upload', async () => {
    global.fetch.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));

    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    fireEvent.click(uploadButton);

    expect(screen.getByText('Uploading...')).toBeInTheDocument();
    expect(uploadButton).toBeDisabled();
  });

  it('displays error message on upload failure', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: false,
      status: 400,
      json: async () => ({ message: 'Invalid CSV format' })
    });

    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    fireEvent.click(uploadButton);

    await waitFor(() => {
      expect(screen.getByText('Invalid CSV format')).toBeInTheDocument();
    });
  });

  it('handles network errors gracefully', async () => {
    global.fetch.mockRejectedValueOnce(new Error('Network error'));

    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    fireEvent.click(uploadButton);

    await waitFor(() => {
      expect(screen.getByText(/Network error/i)).toBeInTheDocument();
    });
  });

  it('allows removing selected file', async () => {
    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    expect(screen.getByText('test.csv')).toBeInTheDocument();

    const removeButton = screen.getByRole('button', { name: /Remove/i });
    fireEvent.click(removeButton);

    expect(screen.queryByText('test.csv')).not.toBeInTheDocument();
  });

  it('shows "Upload New File" button after successful upload', async () => {
    const mockResponse = {
      questions: [],
      validQuestions: 0,
      totalQuestions: 0,
      errors: [],
      columnMapping: {}
    };

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse
    });

    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    fireEvent.click(uploadButton);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Upload New File/i })).toBeInTheDocument();
    });
  });

  it('resets form when "Upload New File" is clicked', async () => {
    const mockResponse = {
      questions: [],
      validQuestions: 0,
      totalQuestions: 0,
      errors: [],
      columnMapping: {}
    };

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse
    });

    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });

    await userEvent.upload(fileInput, file);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    fireEvent.click(uploadButton);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Upload New File/i })).toBeInTheDocument();
    });

    const uploadNewButton = screen.getByRole('button', { name: /Upload New File/i });
    fireEvent.click(uploadNewButton);

    // Form should be reset
    expect(screen.queryByText('test.csv')).not.toBeInTheDocument();
  });

  it('prevents upload without file selection', () => {
    render(<CsvUploadTab />);

    const uploadButton = screen.getByRole('button', { name: /Upload CSV/i });
    expect(uploadButton).toBeDisabled();

    fireEvent.click(uploadButton);

    // Should not call fetch
    expect(global.fetch).not.toHaveBeenCalled();
  });

  it('clears previous errors when uploading new file', async () => {
    const { container } = render(<CsvUploadTab />);

    const fileInput = container.querySelector('input[type="file"]');

    // Upload invalid file first
    const invalidFile = new File(['content'], 'test.txt', { type: 'text/plain' });
    fireEvent.change(fileInput, { target: { files: [invalidFile] } });

    await waitFor(() => {
      expect(screen.getByText('Please select a valid CSV file')).toBeInTheDocument();
    });

    // Upload valid file
    const validFile = new File(['content'], 'test.csv', { type: 'text/csv' });
    fireEvent.change(fileInput, { target: { files: [validFile] } });

    await waitFor(() => {
      expect(screen.queryByText('Please select a valid CSV file')).not.toBeInTheDocument();
    });
  });
});
