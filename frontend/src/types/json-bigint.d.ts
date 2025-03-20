// src/types/json-bigint.d.ts
declare module 'json-bigint' {
  function JSONBig(options?: {
    strict?: boolean
    storeAsString?: boolean
    useNativeBigInt?: boolean
  }): {
    parse: <T = any>(
      text: string,
      reviver?: (key: string, value: any) => any
    ) => T
    stringify: (
      value: any,
      replacer?: (key: string, value: any) => any,
      space?: string | number
    ) => string
  }

  export default JSONBig
}
