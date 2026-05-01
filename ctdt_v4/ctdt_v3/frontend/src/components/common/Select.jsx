import React from'react'
import'./Select.css'
export default function Select({label,error,options=[],placeholder,id,className='',...rest}){
  const sid=id||(label?label.toLowerCase().replace(/\s+/g,'-'):undefined)
  return<div className={`select-wrap${error?' select-wrap--error':''} ${className}`}>
    {label&&<label htmlFor={sid}>{label}</label>}
    <div className="select-box">
      <select id={sid} className="select-field" {...rest}>
        {placeholder&&<option value="" disabled>{placeholder}</option>}
        {options.map(o=><option key={o.value} value={o.value}>{o.label}</option>)}
      </select>
      <span className="select-chevron" aria-hidden="true">▾</span>
    </div>
    {error&&<span className="select-msg select-msg--error">{error}</span>}
  </div>
}
