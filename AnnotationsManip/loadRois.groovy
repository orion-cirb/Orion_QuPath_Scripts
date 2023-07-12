// Manage Imports
import qupath.lib.gui.QuPathGUI
import qupath.lib.gui.viewer.QuPathViewerPlus
import qupath.lib.objects.PathObject
import qupath.lib.objects.hierarchy.PathObjectHierarchy
import qupath.lib.regions.RegionRequest
import qupathj.*
import ij.plugin.frame.RoiManager
import static qupath.imagej.tools.IJTools.convertToImagePlus
import static qupath.lib.scripting.QP.*
import org.apache.commons.io.FilenameUtils

// START OF SCRIPT
clearAllObjects()

def imageData = getCurrentImageData()
def imagePath = new File(imageData.getServer().getPath()).getParent()
def roisPath = buildFilePath(new File(imagePath).getParent(),'Rois')

def imageName = FilenameUtils.removeExtension(imageData.getServerPath())
imageName = FilenameUtils.getBaseName(imageName)
def roiPath = buildFilePath(roisPath,imageName+'.zip')
def roiName = roiPath.split(':')
def roiFile = roiName[roiName.size()-1]

if (fileExists(roiFile)) {
    def server = getCurrentImageData().getServer()
    def rm = RoiManager.getRoiManager() ?: new RoiManager(false)
    rm.runCommand("Open", roiFile)
    def rois = rm.getRoisAsArray() as List
    def request = RegionRequest.createInstance(server, 1)
    def img = convertToImagePlus(server, request)
    def QuPathViewerPlus viewer = QuPathGUI.getInstance().getViewer()
    List<PathObject> pathObjects = QuPath_Send_Overlay_to_QuPath.createObjectsFromROIs(img.getImage(), rois, 1, false, false, viewer.getImagePlane())
    PathObjectHierarchy hierarchy = getCurrentImageData().getHierarchy();
    hierarchy.addObjects(pathObjects)
    rm.close()
    selectAllObjects()
}
