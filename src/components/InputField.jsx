import React from 'react';

/**
 * Large input field for big integer input
 * @param {Object} props
 * @param {string} props.value - Current input value
 * @param {function} props.onChange - Change handler
 * @param {string} props.placeholder - Placeholder text
 * @param {string} props.label - Input label
 * @param {number} props.digitCount - Number of digits
 * @param {boolean} props.disabled - Whether input is disabled
 */
const InputField = ({ 
  value, 
  onChange, 
  placeholder = "Enter a number...",
  label,
  digitCount = 0,
  disabled = false 
}) => {
  return (
    <div className="space-y-2 animate-fade-in">
      <div className="flex justify-between items-center">
        <label className="text-sm font-medium text-muted-foreground">
          {label}
        </label>
        <span className="digit-counter">
          {digitCount.toLocaleString()} digit{digitCount !== 1 ? 's' : ''}
        </span>
      </div>
      <div className="relative">
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          disabled={disabled}
          className="input-field w-full px-4 py-4 text-lg rounded-lg border 
                     focus:outline-none scrollbar-thin
                     overflow-x-auto whitespace-nowrap
                     disabled:opacity-50 disabled:cursor-not-allowed
                     placeholder:text-muted-foreground/50"
          style={{ 
            textOverflow: 'clip',
          }}
          autoComplete="off"
          spellCheck="false"
        />
        {value && (
          <button
            type="button"
            onClick={() => onChange('')}
            className="absolute right-3 top-1/2 -translate-y-1/2 
                       text-muted-foreground hover:text-foreground
                       transition-colors p-1 rounded-md hover:bg-secondary/50"
            aria-label="Clear input"
          >
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              width="16" 
              height="16" 
              viewBox="0 0 24 24" 
              fill="none" 
              stroke="currentColor" 
              strokeWidth="2" 
              strokeLinecap="round" 
              strokeLinejoin="round"
            >
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        )}
      </div>
    </div>
  );
};

export default InputField;
