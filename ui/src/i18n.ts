import { useEffect, useRef } from "react"
import { initReactI18next } from "react-i18next"
import { useTranslation } from "react-i18next"

import i18n from "i18next"
import LanguageDetector from "i18next-browser-languagedetector"
import Backend from "i18next-http-backend"

// don't want to use this?
// have a look at the Quick start guide
// for passing in lng and translations on init

i18n
  // load translation using http -> see /public/locales (i.e. https://github.com/i18next/react-i18next/tree/master/example/react/public/locales)
  // learn more: https://github.com/i18next/i18next-http-backend
  // want your translations to be loaded from a professional CDN? => https://github.com/locize/react-tutorial#step-2---use-the-locize-cdn
  .use(Backend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    fallbackLng: "pl",
    debug: false,

    partialBundledLanguages: true,
    load: "all",
    preload: ["pl", "en"],
    interpolation: {
      escapeValue: false,
    },
    react: {
      useSuspense: false,
    },
  })

export const useTranslationWithPrev = () => {
  const translation = useTranslation()
  const prevLanguageRef = useRef(translation.i18n.language)

  const hasLanguageChanged =
    prevLanguageRef.current !== translation.i18n.language

  useEffect(() => {
    prevLanguageRef.current = translation.i18n.language
  }, [translation.i18n.language])

  return {
    ...translation,
    prevLanguage: prevLanguageRef.current,
    hasLanguageChanged,
  }
}

export default i18n
