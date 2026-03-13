import React from 'react';

/**
 * Result display box with copy functionality
 * @param {Object} props
 * @param {string} props.result - Calculation result
 * @param {string} props.error - Error message
 * @param {boolean} props.isLoading - Whether calculation is in progress
 * @param {function} props.onCopy - Copy handler
 * @param {boolean} props.copied - Whether result was copied
 * @param {number} props.digitCount - Number of digits in result
 */
const ResultBox = ({
  result,
  error,
  isLoading,
  onCopy,
  copied,
  digitCount = 0,
}) => {
  return (
    <div className="space-y-2 animate-fade-in" style={{ animationDelay: '0.3s' }}>
      <div className="flex justify-between items-center">
        <label className="text-sm font-medium text-muted-foreground">
          Result
        </label>
        {result && (
          <span className="digit-counter">
            {digitCount.toLocaleString()} digit{digitCount !== 1 ? 's' : ''}
          </span>
        )}
      </div>

      <div className="relative">
        {/* Result/Error/Loading display */}
        <div 
          className={`result-box min-h-[80px] p-4 rounded-lg border 
                      flex items-center overflow-x-auto scrollbar-thin
                      ${error ? 'border-destructive/50 bg-destructive/5' : ''}`}
        >
          {isLoading ? (
            <div className="flex items-center gap-3 text-muted-foreground">
              <svg 
                className="loading-spinner" 
                xmlns="http://www.w3.org/2000/svg" 
                width="24" 
                height="24" 
                viewBox="0 0 24 24" 
                fill="none" 
                stroke="currentColor" 
                strokeWidth="2" 
                strokeLinecap="round" 
                strokeLinejoin="round"
              >
                <path d="M21 12a9 9 0 1 1-6.219-8.56"></path>
              </svg>
              <span className="text-sm">Calculating...</span>
            </div>
          ) : error ? (
            <div className="error-message flex items-center gap-2 px-3 py-2 rounded-md w-full">
              <svg 
                xmlns="http://www.w3.org/2000/svg" 
                width="18" 
                height="18" 
                viewBox="0 0 24 24" 
                fill="none" 
                stroke="currentColor" 
                strokeWidth="2" 
                strokeLinecap="round" 
                strokeLinejoin="round"
              >
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
              </svg>
              <span className="text-sm">{error}</span>
            </div>
          ) : result ? (
            <div className="font-mono text-lg text-primary break-all whitespace-pre-wrap pr-12">
              {result}
            </div>
          ) : (
            <span className="text-muted-foreground/50 text-sm italic">
              Result will appear here...
            </span>
          )}
        </div>

        {/* Copy button */}
        {result && !error && !isLoading && (
          <button
            onClick={onCopy}
            className="copy-btn absolute right-3 top-1/2 -translate-y-1/2 
                       px-3 py-2 rounded-md text-sm font-medium
                       flex items-center gap-1.5"
            aria-label="Copy result"
          >
            {copied ? (
              <>
                <svg 
                  xmlns="http://www.w3.org/2000/svg" 
                  width="14" 
                  height="14" 
                  viewBox="0 0 24 24" 
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2" 
                  strokeLinecap="round" 
                  strokeLinejoin="round"
                >
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                <span>Copied!</span>
              </>
            ) : (
              <>
                <svg 
                  xmlns="http://www.w3.org/2000/svg" 
                  width="14" 
                  height="14" 
                  viewBox="0 0 24 24" 
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2" 
                  strokeLinecap="round" 
                  strokeLinejoin="round"
                >
                  <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                  <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                </svg>
                <span>Copy</span>
              </>
            )}
          </button>
        )}
      </div>
    </div>
  );
};

export default ResultBox;
