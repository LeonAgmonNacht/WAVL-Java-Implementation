/**
 * Created by Leon on 03/06/2017.
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Collections;
import java.util.List;
import WAVLCore.WAVLTree;

public class WAVLClassTester {
    public ArrayList<WAVLTree.IWAVLNode> leafs=new ArrayList<WAVLTree.IWAVLNode>();
    public WAVLTree generatedTree;
    public int totalInsertRebalances;
    public int totalDeleteRebalances;
    public int maxNumberOfRebalancesInInsert;
    public int maxNumberOfRebalancesInDelete;
    public ArrayList<Integer> keysInTree=new ArrayList<Integer>();

    public void treeGenerator(int size, int IntsLimit) throws Exception {
        Random random=new Random();
        generatedTree=new WAVLTree();
        int numberOfRebalances=0;
        int randomNumber;
        System.out.println("Start inserting elements!");
        for(int i=0;i<size;i++){
            randomNumber=random.nextInt(IntsLimit);
            if(keysInTree.contains(randomNumber)){
                i--;
                continue;
            }
            keysInTree.add(randomNumber);
            numberOfRebalances = generatedTree.insert(randomNumber, Integer.toString(randomNumber));
            totalInsertRebalances=numberOfRebalances>0 ? totalInsertRebalances+numberOfRebalances : totalInsertRebalances;
            if(maxNumberOfRebalancesInInsert<numberOfRebalances)
                maxNumberOfRebalancesInInsert=numberOfRebalances;
            if((i==((int) size*3/4)) || (i==(int) size/2)){
                System.out.println(Integer.toString(i)+" elements were inserted to the tree\n##Testing ranks and ranks differences");
                RankTest(0);	//Calls rank test method (Insert version)
            }
        }
        System.out.println("all elements were inserted to the tree\n##Testing ranks and ranks differences one last time!! XDDDDD");
        printTree();
        RankTest(0);		//Calls rank test method (Insert version)

        System.out.println("done inserting nodes!..");
        //Sort the key's list
        keysInTree.sort(new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }

        });
    }

    public void initialize(){
        this.generatedTree=null;
        this.keysInTree=new ArrayList<Integer>();
        this.leafs=new ArrayList<WAVLTree.IWAVLNode>();
        this.totalDeleteRebalances=0;
        this.totalInsertRebalances=0;
    }
    public void testSize(){
        if(generatedTree.size()!=this.keysInTree.size())
            System.err.println("Problem with size method! :/");
        else
            System.out.println("Size method works!");
    }
    public void testEmpty(){
        if(!generatedTree.empty())
            System.err.println("Problem with empty method! :/");
        else
            System.out.println("Empty method works!");
    }

    public void treeDelete(){
        int RebalancesInSingleDelete=0;
        System.out.println("Start deleting nodes (in keys order)!");
        int firstTestItem=keysInTree.get((int) (keysInTree.size()/2));				//Test ranks and rank-diff after deleting 1/2 of the tree
        int secondTestItem=keysInTree.get((int) (3*keysInTree.size()/4));	//Test ranks and rank-diff after deleting 3/4 of the tree
        for(int min : keysInTree){
            //System.out.println("deleting " + min); //debugginb
            RebalancesInSingleDelete=generatedTree.delete(min);
	/*		if(keysInTree.size()<50) { // debugging
				printTree();
				printTreeRank();
			}*/
            totalDeleteRebalances=RebalancesInSingleDelete>0 ? RebalancesInSingleDelete+totalDeleteRebalances : totalDeleteRebalances;
            if(maxNumberOfRebalancesInDelete<RebalancesInSingleDelete)
                maxNumberOfRebalancesInDelete=RebalancesInSingleDelete;
            if(min==firstTestItem){
                System.out.println(((int) (keysInTree.size()/2))+" elements were deleted from the tree\n##Testing ranks and ranks differences");
                RankTest(1);	//Calls rank test method (regular version)
            }
            else if(min==secondTestItem){
                System.out.println(((int) (3*keysInTree.size()/4))+" elements were deleted from the tree\n##Testing ranks and ranks differences");
                RankTest(1);		//Calls rank test method (regular version)
            }
        }
        System.out.println("Done deleting nodes.. ;)");
    }
    public void treeDeleteShuffle(){
        int RebalancesInSingleDelete=0;
        System.out.println("Start deleting nodes (in keys order)!");
        Collections.shuffle(keysInTree,new Random(System.nanoTime()));
        int firstTestItem=keysInTree.get((int) (keysInTree.size()/2));				//Test ranks and rank-diff after deleting 1/2 of the tree
        int secondTestItem=keysInTree.get((int) (3*keysInTree.size()/4));	//Test ranks and rank-diff after deleting 3/4 of the tree
        for(int min : keysInTree){
            if(keysInTree.size()<50) {
                System.out.println("deleting " + min);
            }
            RebalancesInSingleDelete=generatedTree.delete(min);
            if(keysInTree.size()<50) {
                printTree();
                printTreeRank();
            }
            totalDeleteRebalances=RebalancesInSingleDelete>0 ? RebalancesInSingleDelete+totalDeleteRebalances : totalDeleteRebalances;
            if(maxNumberOfRebalancesInDelete<RebalancesInSingleDelete)
                maxNumberOfRebalancesInDelete=RebalancesInSingleDelete;
            if(min==firstTestItem){
                System.out.println(((int) (keysInTree.size()/2))+" elements were deleted from the tree\n##Testing ranks and ranks differences");
                RankTest(1);	//Calls rank test method (regular version)
            }
            else if(min==secondTestItem){
                System.out.println(((int) (3*keysInTree.size()/4))+" elements were deleted from the tree\n##Testing ranks and ranks differences");
                RankTest(1);		//Calls rank test method (regular version)
            }
        }
        System.out.println("Done deleting nodes.. ;)");
    }


    public void testExistanceOfElements(){
        if(generatedTree.size() !=keysInTree.size())
            System.err.println("There is some problem with your insert method :'(");
        for(int key:keysInTree){
            String val = generatedTree.search(key);
            if(val==null||!val.equals(Integer.toString(key))){
                System.err.println("There is some problem with your insert method :'(");
                System.exit(1);
            }
        }
        System.out.println("All elements are in the tree! Good job.. :)");
    }

    public void createLeafList(){
        leafs=new ArrayList<WAVLTree.IWAVLNode>();
        int[] array=generatedTree.keysToArray();
        for(int i:array){
            WAVLTree.IWAVLNode node = generatedTree.searchNode(i);
            if(node.getKey()==i&&node.getLeft()==null&&node.getRight()==null)
                leafs.add(node);
        }
    }

    public void RankTest(int i){
        createLeafList();
        boolean RanksTest=true;
        boolean RankDifferenceTest=true;
        if(i==1){
            for(WAVLTree.IWAVLNode node:leafs){
                RanksTest=RanksTest&&RanksTest(node);
                RankDifferenceTest=RankDifferenceTest&&RankDifferenceTest(node);
            }
        }else if(i==0){
            for(WAVLTree.IWAVLNode node:leafs){
                RanksTest=RanksTest&&RanksTestInsertOnly(node);
                RankDifferenceTest=RankDifferenceTest&&RankDifferenceTest(node);
            }

        }
        if(!RanksTest)
            System.err.println("Problem with ranks... :/");
        else
            System.out.println("Ranks are fine so far! ;)");
        if(!RankDifferenceTest)
            System.err.println("Problem with rank difference... :/");
        else
            System.out.println("Rank diff 'r great!!! XD");
    }


    public boolean RankDifferenceTest(WAVLTree.IWAVLNode node){
        while(node!=null){
            int leftDiff=node.getLeft()!=null ? node.getRank()-node.getLeft().getRank() : node.getRank()+1;
            int rightDiff=node.getRight()!=null ? node.getRank()-node.getRight().getRank() : node.getRank()+1;
            if((rightDiff!=1&&rightDiff!=2)&&(leftDiff!=1&&leftDiff!=2)){
                return false;
            }
            node=node.getFather();
        }
        return true;
    }

    //Ranks test, regular version!
    //In regular version the method check whether each node's rank is max(node.left,node.right)+1 or max(node.left,node.right)+2

    public boolean RanksTest(WAVLTree.IWAVLNode node){
        while(node!=null){
            int leftChildsRank=node.getLeft()!=null?node.getLeft().getRank():-1;
            int rightChildsRank=node.getRight()!=null?node.getRight().getRank():-1;
            if((node.getRank()!=Math.max(rightChildsRank, leftChildsRank)+1)&&
                    (node.getRank()!=Math.max(rightChildsRank, leftChildsRank)+2))
                return false;
            node=node.getFather();
        }
        return true;
    }
    //Ranks test, insert-only version!
    //In insert only version the method check whether each node's rank is max(node.left,node.right)+1
    public boolean RanksTestInsertOnly(WAVLTree.IWAVLNode node){
        while(node!=null){
            int leftChildsRank=node.getLeft()!=null?node.getLeft().getRank():-1;
            int rightChildsRank=node.getRight()!=null?node.getRight().getRank():-1;
            if(node.getRank()!=Math.max(rightChildsRank, leftChildsRank)+1)
                return false;
            node=node.getFather();
        }
        return true;
    }

    public void testMinAndMax(){
        if(!this.generatedTree.max().equals(Integer.toString(this.keysInTree.get(this.keysInTree.size()-1))))
            System.err.println("Problem with max method.. :/");
        if(!this.generatedTree.min().equals(Integer.toString(this.keysInTree.get(0))))
            System.err.println("Problem with min method.. :/");
        System.out.println("Min&Max methods work.. ^_^");
    }
    public void testKeysToArrayAndInfoToArray(){
        String[] infoToArray=generatedTree.infoToArray();
        int[] keysToArray=generatedTree.keysToArray();
        int cnt=0;
        for(int i : keysInTree){
            if(!infoToArray[cnt].equals(Integer.toString(i))){
                System.err.println("Problem with infoToArray method!! :S");
                System.exit(1);
            }
            if(keysToArray[cnt]!=i){
                System.err.println("Problem with keysToArrat method!! :S");
                System.exit(1);
            }
            cnt++;
        }
        System.out.println("infoToArray & keysToArray methods work.");

    }
    public void printTree(){
        TreePrint printer=new TreePrint();
        printer.printNode(generatedTree.getRoot());
    }

    public void printTreeRank(){
        TreePrint printer=new TreePrint();
        printer.printNodeRank(generatedTree.getRoot());
    }


    public void printInfo(){
        DecimalFormat df=new DecimalFormat();
        df.setMaximumFractionDigits(2);
        System.out.println("********************************************************");
        System.out.println("Number of inserts: "+keysInTree.size());
        if(maxNumberOfRebalancesInInsert>2*Math.log10(keysInTree.size())/Math.log10(2)){
            System.err.println("2*log("+keysInTree.size()+")= "+df.format(2*(Math.log10(keysInTree.size())/Math.log10(2)))+
                    "\nWorst case in insert is greater than log(n)!\nCheck your rebalance's methods!");
        }
        System.out.println("Number of rebalancing operations in all inserts: "+totalInsertRebalances);
        System.out.println("Worst Case rebalancing operations in single insert: "+maxNumberOfRebalancesInInsert);
        System.out.println("Average rebalancing operations in insert: "+df.format((double) totalInsertRebalances/keysInTree.size()));
        if(maxNumberOfRebalancesInDelete>Math.log10(keysInTree.size())/Math.log10(2)){
            System.err.println("log("+keysInTree.size()+")= "+df.format((Math.log10(keysInTree.size())/Math.log10(2)))+
                    "\nWorst case in delete is greater than log(n)!\nCheck your rebalance's methods!");
        }
        System.out.println("Number of rebalancing operations in all deletes: "+totalDeleteRebalances);
        System.out.println("Worst Case rebalancing operations in single delete: "+maxNumberOfRebalancesInDelete);
        System.out.println("Average rebalancing operations in delete: "+df.format((double) totalDeleteRebalances/keysInTree.size()));
        System.out.println("********************************************************");
        System.out.println();
    }
    public void runTest(int num, int max, int testNum , boolean shuffle) throws Exception {
        System.out.println("************Test "+testNum+"************");
        treeGenerator(num, max);
        if(num<50)
            printTree();
        testSize();
        testExistanceOfElements();
        testKeysToArrayAndInfoToArray();
        testMinAndMax();
        if(shuffle)
            treeDeleteShuffle();
        else
            treeDelete();
        testEmpty();
        printInfo();

    }

    public void run(boolean shuffle) throws Exception {
        runTest(20,100,0,shuffle);
        initialize();
        for(int i=1;i<=10;i++){
            runTest(i*10000, i*500000, i,shuffle);
            initialize();
        }
    }

    class TreePrint {

        public <T extends Comparable<?>> void printNode(
                WAVLTree.IWAVLNode root) {
            int maxLevel = maxLevel(root);

            printNodeInternal(Collections.singletonList(root), 1, maxLevel);
        }

        private <T extends Comparable<?>> void printNodeInternal(
                List<WAVLTree.IWAVLNode> list, int level, int maxLevel) {
            if (list.isEmpty() || isAllElementsNull(list))
                return;

            int floor = maxLevel - level;
            int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

            printWhitespaces(firstSpaces);

            List<WAVLTree.IWAVLNode> newNodes = new ArrayList<WAVLTree.IWAVLNode>();
            for (WAVLTree.IWAVLNode node : list) {
                if (node != null) {
                    System.out.print(node.getKey());
                    newNodes.add(node.getLeft());
                    newNodes.add(node.getRight());
                } else {
                    newNodes.add(null);
                    newNodes.add(null);
                    System.out.print(" ");
                }

                printWhitespaces(betweenSpaces);
            }
            System.out.println("");

            for (int i = 1; i <= endgeLines; i++) {
                for (int j = 0; j < list.size(); j++) {
                    printWhitespaces(firstSpaces - i);
                    if (list.get(j) == null) {
                        printWhitespaces(endgeLines + endgeLines + i
                                + 1);
                        continue;
                    }

                    if (list.get(j).getLeft() != null)
                        System.out.print("/");
                    else
                        printWhitespaces(1);

                    printWhitespaces(i + i - 1);

                    if (list.get(j).getRight() != null)
                        System.out.print("\\");
                    else
                        printWhitespaces(1);

                    printWhitespaces(endgeLines + endgeLines - i);
                }

                System.out.println("");
            }

            printNodeInternal(newNodes, level + 1, maxLevel);
        }

        public <T extends Comparable<?>> void printNodeRank(
                WAVLTree.IWAVLNode root) {
            int maxLevel = maxLevel(root);

            printNodeInternalRank(Collections.singletonList(root), 1, maxLevel);
        }

        private <T extends Comparable<?>> void printNodeInternalRank(
                List<WAVLTree.IWAVLNode> list, int level, int maxLevel) {
            if (list.isEmpty() || isAllElementsNull(list))
                return;

            int floor = maxLevel - level;
            int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

            printWhitespaces(firstSpaces);

            List<WAVLTree.IWAVLNode> newNodes = new ArrayList<WAVLTree.IWAVLNode>();
            for (WAVLTree.IWAVLNode node : list) {
                if (node != null) {
                    System.out.print(node.getRank());
                    newNodes.add(node.getLeft());
                    newNodes.add(node.getRight());
                } else {
                    newNodes.add(null);
                    newNodes.add(null);
                    System.out.print(" ");
                }

                printWhitespaces(betweenSpaces);
            }
            System.out.println("");

            for (int i = 1; i <= endgeLines; i++) {
                for (int j = 0; j < list.size(); j++) {
                    printWhitespaces(firstSpaces - i);
                    if (list.get(j) == null) {
                        printWhitespaces(endgeLines + endgeLines + i
                                + 1);
                        continue;
                    }

                    if (list.get(j).getLeft() != null)
                        System.out.print("/");
                    else
                        printWhitespaces(1);

                    printWhitespaces(i + i - 1);

                    if (list.get(j).getRight() != null)
                        System.out.print("\\");
                    else
                        printWhitespaces(1);

                    printWhitespaces(endgeLines + endgeLines - i);
                }

                System.out.println("");
            }

            printNodeInternalRank(newNodes, level + 1, maxLevel);
        }

        private void printWhitespaces(int count) {
            for (int i = 0; i < count; i++)
                System.out.print(" ");
        }

        private <T extends Comparable<?>> int maxLevel(WAVLTree.IWAVLNode root) {
            if (root == null)
                return 0;

            return Math.max(maxLevel(root.getLeft()),
                    maxLevel(root.getRight())) + 1;
        }

        private <T> boolean isAllElementsNull(List<T> list) {
            for (Object object : list) {
                if (object != null)
                    return false;
            }

            return true;
        }

    }

}
