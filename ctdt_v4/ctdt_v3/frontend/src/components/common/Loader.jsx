import React from'react'
import'./Loader.css'
export default function Loader({size='md',center=false,label}){
  const s=<span className={`loader loader--${size}`} role="status" aria-label={label||'Loading'}/>
  if(!center)return s
  return<div className="loader-center">{s}{label&&<span className="loader-label">{label}</span>}</div>
}
