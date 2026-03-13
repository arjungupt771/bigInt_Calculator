import React from 'react';

/**
 * Operation buttons component
 * @param {Object} props
 * @param {function} props.onAdd - Addition handler
 * @param {function} props.onSubtract - Subtraction handler
 * @param {function} props.onMultiply - Multiplication handler
 * @param {function} props.onDivide - Division handler
 * @param {function} props.onModulo - Modulo handler
 * @param {function} props.onClear - Clear handler
 * @param {boolean} props.disabled - Whether buttons are disabled
 * @param {boolean} props.isLoading - Whether calculation is in progress
 * @param {string} props.lastOperation - Last performed operation
 */
const OperationButtons = ({
  onAdd,
  onSubtract,
  onMultiply,
  onDivide,
  onModulo,
  onClear,
  disabled = false,
  isLoading = false,
  lastOperation = null,
}) => {
  const operations = [
    { 
      id: 'add', 
      label: '+', 
      name: 'Add',
      onClick: onAdd, 
      className: 'btn-add',
      ariaLabel: 'Addition'
    },
    { 
      id: 'subtract', 
      label: '−', 
      name: 'Subtract',
      onClick: onSubtract, 
      className: 'btn-subtract',
      ariaLabel: 'Subtraction'
    },
    { 
      id: 'multiply', 
      label: '×', 
      name: 'Multiply',
      onClick: onMultiply, 
      className: 'btn-multiply',
      ariaLabel: 'Multiplication'
    },
    { 
      id: 'divide', 
      label: '÷', 
      name: 'Divide',
      onClick: onDivide, 
      className: 'btn-divide',
      ariaLabel: 'Division'
    },
    { 
      id: 'modulo', 
      label: '%', 
      name: 'Modulo',
      onClick: onModulo, 
      className: 'btn-modulo',
      ariaLabel: 'Modulo'
    },
  ];

  return (
    <div className="space-y-4 animate-fade-in" style={{ animationDelay: '0.2s' }}>
      {/* Operation buttons */}
      <div className="grid grid-cols-5 gap-2 sm:gap-3">
        {operations.map((op) => (
          <button
            key={op.id}
            onClick={op.onClick}
            disabled={disabled || isLoading}
            aria-label={op.ariaLabel}
            className={`operation-btn ${op.className} 
                       relative flex flex-col items-center justify-center
                       py-4 sm:py-5 px-2 rounded-xl text-foreground
                       ${lastOperation === op.id ? 'ring-2 ring-foreground/30' : ''}`}
          >
            <span className="text-2xl sm:text-3xl font-bold leading-none">
              {op.label}
            </span>
            <span className="text-[10px] sm:text-xs mt-1 opacity-80 font-medium">
              {op.name}
            </span>
          </button>
        ))}
      </div>

      {/* Clear button */}
      <button
        onClick={onClear}
        className="btn-clear operation-btn w-full py-3 rounded-xl 
                   text-muted-foreground hover:text-foreground
                   flex items-center justify-center gap-2
                   border border-border/50"
      >
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
          <path d="M3 6h18"></path>
          <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
          <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
          <line x1="10" y1="11" x2="10" y2="17"></line>
          <line x1="14" y1="11" x2="14" y2="17"></line>
        </svg>
        <span className="font-medium">Clear All</span>
      </button>
    </div>
  );
};

export default OperationButtons;
