import React from'react'
import'./Input.css'
export default function Input({label,error,helpText,id,className='',...rest}){
  const inputId=id||(label?label.toLowerCase().replace(/\s+/g,'-'):undefined)
  return<div className={`input-wrap${error?' input-wrap--error':''} ${className}`}>
    {label&&<label htmlFor={inputId}>{label}</label>}
    <input id={inputId} className="input-field" {...rest}/>
    {error&&<span className="input-msg input-msg--error">{error}</span>}
    {helpText&&!error&&<span className="input-msg">{helpText}</span>}
  </div>
}
