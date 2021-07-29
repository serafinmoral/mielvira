JAVAC=javac
FLAGS_JAVA= -deprecation
 
FUENTES = AbductiveInference.java AbductiveInferenceNilsson.java AIPartitionElement.java AIPartitionElementList.java ASCII_CharStream.java BayesNetConstants.java BayesNetParse.java BayesNetParseConstants.java BayesNetParseTokenManager.java BayesNetTokenManager.java BinaryTreeProp.java Bnet.java CliquePair.java CliquePairList.java Configuration.java ConditionalIndependence.java DSeparation.java EditVariableDialog.java Elvira.java ElviraFrame.java ElviraPopupMenu.java ElviraHelpMessages.java Evidence.java EvidenceParse.java EvidenceParseConstants.java EvidenceParseTokenManager.java Explanation.java FSEditFunctionDialog.java FiniteStates.java GeneralDialog.java Graph.java  HuginPropagation.java ImportanceSampling.java ImportanceSamplingTable.java ImportanceSamplingTree.java  ImportanceSamplingTreeAV.java JoinTree.java Link.java LinkList.java MainMenu.java MultipleTree.java NeighbourTree.java NeighbourTreeList.java Network.java Node.java NodeJoinTree.java NodeList.java NodePairTable.java NodeQueue.java NodeQueueM.java ObservedNodeDialog.java OpenURLDialog.java PairTable.java ParseException.java Potential.java PotentialConvexSet.java PotentialMTree.java PotentialTable.java PotentialTree.java PriorityQueue.java PriorityQueueM.java ProbabilityTree.java Propagation.java QuitDialog.java QuitEditorDialog.java Relation.java RelationList.java ResultParse.java ResultParseConstants.java ResultParseTokenManager.java SetVectorOperations.java SimulationProp.java SystematicSamplingTable.java SystematicSamplingTree.java Token.java TokenMgrError.java Triangulation.java VariableElimination.java VEWithPotentialTree.java WindowMenuItemListener.java DataBaseCases.java DataBaseParse.java CaseList.java CaseListMem.java CaseListOutMem.java PCLearning.java Learning.java K2Learning.java BBenedict.java LogFactorial.java

Elvira : $(FUENTES:java=class)

%.class : %.java
	$(JAVAC) $(FLAGS_JAVA) $<

