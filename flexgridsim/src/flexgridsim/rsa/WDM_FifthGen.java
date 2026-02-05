/**
'	 * 5G data traffic routing over Elastic Optical Networks
 */
package flexgridsim.rsa;
import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Element;

import flexgridsim.Flow;
import flexgridsim.LightPath;
import flexgridsim.Path;
import flexgridsim.PhysicalTopology;
import flexgridsim.Slot;
import flexgridsim.TrafficGenerator;
import flexgridsim.VirtualTopology;
import flexgridsim.util.KShortestPaths;
import flexgridsim.util.Modulations;
import flexgridsim.util.MultiGraph;
import flexgridsim.util.WeightedGraph;

/**
 * @author AdrielRodrigues
 *
 */

public class WDM_FifthGen implements RSA  {
	private PhysicalTopology pt;
	private VirtualTopology vt;
	private ControlPlaneForRSA cp;
	private WeightedGraph graph;
	
	// Active Nodes
	private int mainCO;
	private ArrayList<Integer> clouds = new ArrayList<>();
	private ArrayList<Integer> fogs = new ArrayList<>();
	
	private Random random = new Random();

	@Override
	public void flowDeparture(Flow flow) {
		// TODO Cria um novo fluxo pós processamento
	}

	@Override
	public void simulationInterface(Element xml, PhysicalTopology pt,
			VirtualTopology vt, ControlPlaneForRSA cp, TrafficGenerator traffic) {
		this.pt = pt;
		this.vt = vt;
		this.cp = cp;
		this.graph = pt.getWeightedGraph();
		
		mainCO = random.nextInt(20) + 80;
		clouds.add(mainCO);
	}

	@Override
	public void flowArrival(Flow flow) {
		long id=-1;
		int guardBand=1;
		if(pt.getGrooming())
			guardBand=0;
		
		int demandInSlots;
		Path path = null;
		
		int mod = 0;
		demandInSlots = (int) Math.ceil((flow.getRate()/1000.0) / (double) Modulations.getBandwidth(mod)) + guardBand;
		
		int cos = flow.getCOS();
		
		if (cos == 0) {
			path = findFog(flow, cos, demandInSlots);
			if (path == null)
				path = upFog(flow, cos, demandInSlots);
		} else {
			path = findCloud(flow, cos, demandInSlots);
			if (path == null) {
				path = upCloud(flow, cos, demandInSlots);
			}
		}
		if (path == null) {
			cp.blockFlow(flow.getID());
			return;
		}
		managementNode(flow, path, mod, id);
	}
	
	public Path findCloud(Flow flow, int cos, int demandInSlots) {
		Path tempPath;
		for (int cloud : this.clouds) {
			tempPath = getKShortestPath(graph, flow.getSource(), cloud, cos, demandInSlots, false);
			if (tempPath == null) {
				continue;
			}
			int delay = 5 * getPhysicalDistance(tempPath.getLinks());
			
			if ((cos == 1) && (delay <= 100)) {
				return tempPath;
			} else if ((cos == 2) && (delay <= 250)) {
				return tempPath;
			}
		}
		return null;
	}
	
	public Path upCloud(Flow flow, int cos, int demandInSlots) {
		Path tempPath;
		// Core Nodes
		for (int i = 80; i < 100; i++) {
			tempPath = getKShortestPath(graph, flow.getSource(), i, cos, demandInSlots, false);
			if (tempPath == null) {
				continue;
			}
			int delay = 5 * getPhysicalDistance(tempPath.getLinks());
			
			if ((cos == 1) && (delay <= 100)) {
				if (!this.clouds.contains(i)) {
					this.clouds.add(i);
				}
				return tempPath;
			} else if ((cos == 2) && (delay <= 250)) {
				if (!this.clouds.contains(i)) {
					this.clouds.add(i);
				}
				return tempPath;
			}
		}
		return null;
	}
	
	public Path findFog(Flow flow, int cos, int demandInSlots) {
		Path tempPath;
		for (int fogNode : this.fogs) {
			tempPath = getKShortestPath(graph, flow.getSource(), fogNode, cos, demandInSlots, false);
			if (tempPath == null)
				continue;
			int delay = 5 * getPhysicalDistance(tempPath.getLinks());
			
			if (delay <= 50) 
				return tempPath;
		}
		return null;
	}
	
	public Path upFog(Flow flow, int cos, int demandInSlots) {
		Path tempPath;
		// Core Nodes
		for (int i = 50; i < 80; i++) {
			tempPath = getKShortestPath(graph, flow.getSource(), i, cos, demandInSlots, false);
			if (tempPath == null) {
				continue;
			}
			int delay = 5 * getPhysicalDistance(tempPath.getLinks());
			
			if (delay <= 50) {
				if (!this.fogs.contains(i)) {
					this.fogs.add(i);
					return tempPath;
				}
			}
		}
		return null;
	}
	
	
	public void managementNode (Flow flow, Path path, int modulation, long id) {
		id = vt.createLightpath(path, modulation);
		ArrayList<LightPath> lightpath = new ArrayList<LightPath>();
			
		if (id >= 0) {
			flow.setLinks(path.getLinks());
			flow.setSlotList(path.getSlotList());
			flow.setModulationLevel(modulation);
			lightpath.add(vt.getLightpath(id));
		}
		if(id<0){
			cp.blockFlow(flow.getID());
			return;
		}
			
		if(!cp.acceptFlow(flow.getID(), lightpath)) {
			vt.removeLightPath(id);
			cp.blockFlow(flow.getID());
			return;
		}	
		return;
	}

	public int getPhysicalDistance(int[] links){
		if(links!=null&& links.length>0){
			int physicalDistance = 0;
			for (int i = 0; i < links.length; i++) {
				physicalDistance += pt.getLink(links[i]).getDistance();
			}
			return physicalDistance;
		}
		else
			return -1;
	}

	public Path getKShortestPath(WeightedGraph G,int src, int dst, int cos, int demand, boolean overlap){
		KShortestPaths kShortestPaths = new KShortestPaths();
		int[][] kPaths = kShortestPaths.dijkstraKShortestPaths(G, src, dst, 3);
		if(kPaths==null)
			return null;
		int []links;
		ArrayList<Slot> channel = new ArrayList<Slot>();
		
		for (int i = 0; i < kPaths.length; i++) {
			if (kPaths[i].length > 1){
				links = new int[kPaths[i].length - 1];
				for (int j = 0; j < kPaths[i].length - 1; j++) {
					links[j] = pt.getLink(kPaths[i][j], kPaths[i][j + 1]).getID();
				}
				channel=getSimilarSlotsInLinks(links, cos, overlap, demand);
				if(channel!=null){
					return new Path(links, channel);
				}	
			} else {
				continue;
			}
		}
		return null;
	}
	
	
	public  ArrayList<Slot> getSimilarSlotsInLinks(int []links, int cos, boolean sharing, int demandInSlots) {
		ArrayList<Slot> channel = new ArrayList<Slot>();
		int firstSlot;
		int lastSlot;
		int core;
		
		int regionStart = 0;
		int regionFinish = pt.getNumSlots();
		
		// Política de restrição
		if (cos == 0) {
			regionStart = 0;
			regionFinish = pt.getNumSlots() / 4;
		} else if (cos == 1) {
			regionStart = pt.getNumSlots() / 4;
			regionFinish = 3 * (pt.getNumSlots()/4);
		} else if (cos == 2) {
			regionStart = 3 * (pt.getNumSlots()/4);
			regionFinish = pt.getNumSlots();
		}
		
		for (int i = regionStart; i < regionFinish-demandInSlots; i++) {
			firstSlot = i;
			lastSlot = i + demandInSlots - 1;
			core=usingSameCore(firstSlot, lastSlot, links, sharing);
		
			if(core!=-1){
				for (int j = firstSlot; j <= lastSlot; j++) {
					for (int l = 0; l < links.length; l++) {
						channel.add(new Slot(core, j, links[l] ));
					}
				}
				return channel;	
			}//else{@todo}
	
		}	
		return null;
	}
	
	
	public int usingSameCore(int firstSlot, int lastSlot, int links[], boolean sharing){
		for (int core=0; core < pt.getNumCores(); core ++){
			if(freeSlotInAllLinks(links, firstSlot, lastSlot, core, sharing)){
				return core;
			}
		}
		return -1;
	}

	public boolean freeSlotInAllLinks(int links[], int firstSlot, int lastSlot, int core, boolean sharing) {
		for (int j = 0; j < links.length; j++) {
			if (sharing==false){
				for (int h = firstSlot; h <= lastSlot; h++) {
					if(!pt.getLink(links[j]).getSpectrum(core, h)){
						return false;
					}
				}
			}
			if (sharing==true){
				for (int h = firstSlot; h <= lastSlot; h++) {
					if(!pt.getLink(links[j]).getSpectrumS(core, h)){
						return false;
					}
				}
			}
		}
		return true;
	}
	
}
