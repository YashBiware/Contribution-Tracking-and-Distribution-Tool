import React from'react'
import'./Button.css'
export default function Button({variant='primary',size='md',loading=false,block=false,onClick,type='button',disabled=false,children,className='',...rest}){
  const cls=['btn',`btn--${variant}`,`btn--${size}`,block?'btn--block':'',loading?'btn--loading':'',className].filter(Boolean).join(' ')
  return<button className={cls} type={type} disabled={disabled||loading} onClick={onClick} {...rest}>
    {loading&&<span className="btn__spinner" aria-hidden="true"/>}
    <span className="btn__label">{children}</span>
  </button>
}
