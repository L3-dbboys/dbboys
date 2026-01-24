---
hide_title: false
sidebar_position: 8
---
page10
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:10                        PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |10          |1     |0x26  |2     |0x1800|228   |1808  |0           |0           |

     PAGE DATA: (212 BYTES USED,1808 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 48 )
     [ offset   24 len    4 ]Level 0 Archive Began           0
     [ offset   28 len    4 ]Level 0 Time Stamp              0
     [ offset   32 len    4 ]Level 0 Logical Log Unique Id   0
     [ offset   36 len    4 ]Level 0 Logical Log Position    0x0
     [ offset   40 len    4 ]Level 1 Archive Began           0
     [ offset   44 len    4 ]Level 1 Time Stamp              0
     [ offset   48 len    4 ]Level 1 Logical Log Unique Id   0
     [ offset   52 len    4 ]Level 1 Logical Log Position    0x0
     [ offset   56 len    4 ]Level 2 Archive Began           0
     [ offset   60 len    4 ]Level 2 Time Stamp              0
     [ offset   64 len    4 ]Level 2 Logical Log Unique Id   0
     [ offset   68 len    4 ]Level 2 Logical Log Position    0x0

     Slot2: Slot Begin Postion ( 72 ) ,Slot Length ( 156 )
     [ offset   72 len    4 ]DR Ckpt Logical Log Id          -1
     [ offset   76 len    4 ]DR Ckpt Logical Log Pos         0xffffffff
     [ offset   80 len    4 ]DR Last Logical Log Id          -1
     [ offset   84 len    4 ]DR Last Logical Log Page        -1
     [ offset   88 len    4 ]DR Last Mode Change             -1
                             Case Value:
                            -1 Never Defined HAC Mode
                             1 Standard To HAC Primary Mode
                             2 Standard To HAC Secondary Mode
                             3 HAC Primary To Standard Mode
                             4 HAC Secondary To Standard Mode
                             5 HAC Primary Detected Error
                             6 HAC Secondary Detected Error
                             7 HAC Primary Mode
                             8 HAC Secondary
                             9 RHAC Secondary
     [ offset   92 len  136 ]Unused                          

     [ offset 2036 len    4 ]Slot  2: [2B]Slot->sl_ptr=  72,[2B]Slot->sl_len= 156
     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len=  48

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |       0x2d|
     ##################################################################################
```
page11
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:11                        PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |11          |1     |0x21  |2     |0x1800|228   |1808  |0           |0           |

     PAGE DATA: (212 BYTES USED,1808 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 48 )
     [ offset   24 len    4 ]Level 0 Archive Began           0
     [ offset   28 len    4 ]Level 0 Time Stamp              0
     [ offset   32 len    4 ]Level 0 Logical Log Unique Id   0
     [ offset   36 len    4 ]Level 0 Logical Log Position    0x0
     [ offset   40 len    4 ]Level 1 Archive Began           0
     [ offset   44 len    4 ]Level 1 Time Stamp              0
     [ offset   48 len    4 ]Level 1 Logical Log Unique Id   0
     [ offset   52 len    4 ]Level 1 Logical Log Position    0x0
     [ offset   56 len    4 ]Level 2 Archive Began           0
     [ offset   60 len    4 ]Level 2 Time Stamp              0
     [ offset   64 len    4 ]Level 2 Logical Log Unique Id   0
     [ offset   68 len    4 ]Level 2 Logical Log Position    0x0

     Slot2: Slot Begin Postion ( 72 ) ,Slot Length ( 156 )
     [ offset   72 len    4 ]DR Ckpt Logical Log Id          -1
     [ offset   76 len    4 ]DR Ckpt Logical Log Pos         0xffffffff
     [ offset   80 len    4 ]DR Last Logical Log Id          -1
     [ offset   84 len    4 ]DR Last Logical Log Page        -1
     [ offset   88 len    4 ]DR Last Mode Change             -1
                             Case Value:
                            -1 Never Defined HAC Mode
                             1 Standard To HAC Primary Mode
                             2 Standard To HAC Secondary Mode
                             3 HAC Primary To Standard Mode
                             4 HAC Secondary To Standard Mode
                             5 HAC Primary Detected Error
                             6 HAC Secondary Detected Error
                             7 HAC Primary Mode
                             8 HAC Secondary
                             9 RHAC Secondary
     [ offset   92 len  136 ]Unused                          

     [ offset 2036 len    4 ]Slot  2: [2B]Slot->sl_ptr=  72,[2B]Slot->sl_len= 156
     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len=  48

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |       0x2b|
     ##################################################################################
```