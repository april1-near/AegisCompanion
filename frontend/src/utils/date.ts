export const formatDateTime = (
    isoString: string,
    format = 'yyyy-MM-dd HH:mm:ss'
  ) => {
    const date = new Date(isoString);
    const pad = (n: number) => n.toString().padStart(2, '0');
  
    return format
      .replace('yyyy', date.getFullYear().toString())
      .replace('MM', pad(date.getMonth() + 1))
      .replace('dd', pad(date.getDate()))
      .replace('HH', pad(date.getHours()))
      .replace('mm', pad(date.getMinutes()))
      .replace('ss', pad(date.getSeconds()));
  };
  