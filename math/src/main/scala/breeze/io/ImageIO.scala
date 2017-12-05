package breeze.io

import java.nio.file.{Files, Paths}

import breeze.linalg._
import breeze.numerics._

import java.awt._
import java.awt.color._
import java.awt.image._

import java.io.File

import javax.imageio


object ImageIO {

  def getGrayscaleBufferedImage(width: Int ,  buffer: Array[Byte], column_major:Boolean=true): BufferedImage = {
    val height = buffer.length / width
    val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
    val cm = new ComponentColorModel(cs, Array(8), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE)
    val (pixelStride, scanlineStride) = if (column_major) (height, 1) else (1, width)
    val sm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, pixelStride, scanlineStride, Array(0))
    val db = new DataBufferByte(buffer, width * height)
    val raster = Raster.createWritableRaster(sm, db, null)
    new BufferedImage(cm, raster, false, null)
  }

  def write(filename:String, buff: DenseMatrix[Byte], column_major:Boolean=true) = {
    val image: BufferedImage = getGrayscaleBufferedImage(buff.cols, buff.data, column_major)
    val ouptut: File = new File(filename)
    imageio.ImageIO.write(image, "png", ouptut)
  }

  def read(imageFile: File) ={
    val bufferedImage: BufferedImage  = imageio.ImageIO.read(imageFile)
    val raster: WritableRaster = bufferedImage.getRaster()
    val data: DataBufferByte   = raster.getDataBuffer().asInstanceOf[DataBufferByte]
    new DenseMatrix(raster.getHeight, raster.getWidth, data.getData())
  }
}

object TestImageIO extends App {

  val imagem = new DenseMatrix(10, 10, (0 until 100).map(x=>((x%10)*255/9).toByte).toArray)
  ImageIO.write("teste_colmaj.png", imagem)
  ImageIO.write("teste_rowmaj.png", imagem, false)
}
