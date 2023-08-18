function fileSizeFormatter(value) {
  return Math.round((value / 1024.0 / 1024.0) * 100.0) / 100.0 + "MB";
}

function appNameFormatter(value, row, index, field) {
  return '<a href="' + row["_" + field + "_data"]["href"] + '">' + "<span>" + value + "</span></a>";
}
