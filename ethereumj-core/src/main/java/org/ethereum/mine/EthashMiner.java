package org.ethereum.mine;

import com.google.common.util.concurrent.ListenableFuture;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.BlockHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * The adapter of Ethash for MinerIfc
 *
 * Created by Anton Nashatyrev on 26.02.2016.
 */
@Component
public class EthashMiner implements MinerIfc {

    @Autowired
    SystemProperties config = SystemProperties.CONFIG;

    private int cpuThreads = 2;  // FIXME: the instance is created during SystemProperties <clinit>
    private boolean fullMining = true;

    @PostConstruct
    private void init() {
        cpuThreads = config.getMineCpuThreads();
        fullMining = config.isMineFullDataset();
    }

    @Override
    public ListenableFuture<Long> mine(Block block) {
        init();
        return fullMining ?
                Ethash.getForBlock(block.getNumber()).mine(block, cpuThreads) :
                Ethash.getForBlock(block.getNumber()).mineLight(block, cpuThreads);
    }

    @Override
    public boolean validate(BlockHeader blockHeader) {
        return Ethash.getForBlock(blockHeader.getNumber()).validate(blockHeader);
    }
}
