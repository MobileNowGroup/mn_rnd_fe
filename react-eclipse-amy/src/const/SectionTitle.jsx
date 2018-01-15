
import React from 'react'
import '../styles/css/main.css'

export const SectionTitle = ({title,describe}) => {
  return (
    <div className='section_content'>
      <label className='section_content__title'>{title}</label>
      <label className='section_content__describe'>{describe}</label>
    </div>
  )
}
