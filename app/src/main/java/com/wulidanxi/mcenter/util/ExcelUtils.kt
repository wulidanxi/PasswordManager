package com.wulidanxi.mcenter.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.wulidanxi.mcenter.db.Content
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.util.*

object ExcelUtils {
    private var arial14CellFormat: WritableCellFormat? = null
    private var arial10CellFormat: WritableCellFormat? = null
    private var arial12CellFormat: WritableCellFormat? = null
    private const val UTF8_ENCODING = "UTF-8"

    private fun format() {
        try {
            val arial14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
            arial14font.colour = Colour.LIGHT_BLUE
            arial14CellFormat = WritableCellFormat(arial14font)
            arial14CellFormat!!.alignment = Alignment.CENTRE
            arial14CellFormat!!.setBorder(Border.ALL, BorderLineStyle.THIN)
            arial14CellFormat!!.setBackground(Colour.VERY_LIGHT_YELLOW)
            val arial10font = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
            arial10CellFormat = WritableCellFormat(arial10font)
            arial10CellFormat!!.alignment = Alignment.CENTRE
            arial10CellFormat!!.setBorder(Border.ALL, BorderLineStyle.THIN)
            arial10CellFormat!!.setBackground(Colour.GRAY_25)
            val arial12font = WritableFont(WritableFont.ARIAL, 12)
            arial12CellFormat = WritableCellFormat(arial12font)
            arial12CellFormat!!.alignment = Alignment.CENTRE
            arial12CellFormat!!.setBorder(Border.ALL, BorderLineStyle.THIN)
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }

    fun initExcel(fileName: String, colName: List<String?>) {
        format()
        var workbook: WritableWorkbook? = null
        try {
            val file = File(fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            workbook = Workbook.createWorkbook(file)
            val sheet = workbook.createSheet("密码管家", 0)
            sheet.addCell(Label(0, 0, fileName, arial14CellFormat))
            for (col in colName.indices) {
                sheet.addCell(Label(col, 0, colName[col], arial10CellFormat))
            }
            sheet.setRowView(0, 340)
            workbook.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (workbook != null) {
                try {
                    workbook.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun <T> writeObjListToExcel(objList: List<T>?, fileName: String, mContext: Context?) {
        if (objList != null && objList.isNotEmpty()) {
            var writableWorkbook: WritableWorkbook? = null
            //InputStream inputStream = null;
            try {
                FileInputStream(File(fileName)).use { inputStream ->
                    val setEncoding = WorkbookSettings()
                    setEncoding.encoding = UTF8_ENCODING
                    //inputStream = new FileInputStream(new File(fileName));
                    val workbook = Workbook.getWorkbook(inputStream)
                    writableWorkbook = Workbook.createWorkbook(File(fileName), workbook)
                    if (writableWorkbook != null) {
                        val sheet = writableWorkbook!!.getSheet(0)
                        val list: MutableList<String> =
                            ArrayList()
                        for (j in objList.indices) {
                            val (channel, account, password, date) = objList[j] as Content
                            list.clear()
                            list.add(channel)
                            list.add(account)
                            list.add(password)
                            list.add(date)
                            for (i in list.indices) {
                                sheet.addCell(
                                    Label(
                                        i,
                                        j + 1,
                                        list[i],
                                        arial12CellFormat
                                    )
                                )
                                if (list[i].length <= 4) {
                                    sheet.setColumnView(1, list[i].length + 8)
                                } else {
                                    sheet.setColumnView(1, list[i].length + 5)
                                }
                            }
                            sheet.setRowView(j + 1, 350)
                        }
                        writableWorkbook!!.write()
                        ToastUtils.showShort("导出成功$fileName")
                    }

                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(mContext, "导出失败", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                if (writableWorkbook != null) {
                    try {
                        writableWorkbook!!.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun readExcel(file: File, context: Context?): List<Content>? {
        Log.d("yy", "file=" + file.absolutePath)
        var book: Workbook? = null
        try {
            FileInputStream(file).use { `is` ->
                book = Workbook.getWorkbook(`is`)
                if (book != null) {
                    book!!.numberOfSheets
                    val sheet = book!!.getSheet(0)
                    val rows = sheet.rows
                    val tempList: MutableList<Content> =
                        ArrayList()
                    for (i in 1 until rows) {
                        val channel = sheet.getCell(0, i).contents
                        val account = sheet.getCell(1, i).contents
                        val password = sheet.getCell(2, i).contents
                        val date = sheet.getCell(3, i).contents
                        val content =
                            Content(channel, account, password, date)
                        tempList.add(content)
                        Log.d(
                            "读取到的数据====>",
                            "渠道：$channel//账号：$account//密码：$password//时间：$date"
                        )
                    }
                    return tempList
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (book != null) {
                try {
                    book!!.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}