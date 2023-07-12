// Manage Imports
import ij.plugin.frame.RoiManager
import org.apache.commons.io.FilenameUtils
import static qupath.lib.scripting.QP.*


def imageData = getCurrentImageData()
def imagePath = new File(imageData.getServer().getPath()).getParent()
def roisPath = buildFilePath(new File(imagePath).getParent(),'Rois')

def imageName = FilenameUtils.removeExtension(imageData.getServerPath())
imageName = FilenameUtils.getBaseName(imageName)
def roiPath = buildFilePath(roisPath,imageName+'.zip')
def roiName = roiPath.split(':')
def roiFile = roiName[roiName.size()-1]

def annotations = getAnnotationObjects()
def rm = RoiManager.getRoiManager() ?: new RoiManager(false)
double x = 0
double y = 0
double downsample = 1 // Increase if you want to export to work at a lower resolution
annotations.each {
  def roi = IJTools.convertToIJRoi(it.getROI(), x, y, downsample)
  rm.addRoi(roi)
}
rm.runCommand("Save", roiFile)
rm.close()