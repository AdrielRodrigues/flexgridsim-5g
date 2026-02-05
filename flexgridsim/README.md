# Flexgridsim
##### @author: Edson Adriel Freitas Rodrigues, Daniel Macêdo Batista, Helder May Nunes da Silva Oliveira

This repository contains the FlexgridSim discrete event computer network simulator, which has been enhanced to simulate traffic in the Cloud-Fog Radio Access Network (CF-RAN) model over Space-Division Multiplexing Elastic Optical Networks (SDM-EON). It implements the 5GEON algorithm proposed in the paper <b>Latency-Aware Routing and Multidimensional Optical Resource Allocation for CF-RAN over SDM-EON</b> submitted to the Brazilian Symposium on Computer Networks and Distributed Systems 2026.

### Running Experiments

Usage: FlexGridSim.jar xml_file number_of_simulations [-trace] [-verbose] [minload maxload step]

The required parameters are:
xml_file: the XML file containing all the information about the simulation environment. Example xml files are provided.
number_of_simulations: number of simulations will be ran with a different seed each;

### Plotting

The simulator generates results in .dat files that can be collected in ./flexgridsim/output. The script generates graphs for all output metrics in the simulator.