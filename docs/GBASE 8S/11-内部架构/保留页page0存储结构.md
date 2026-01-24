page0
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:0                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |0           |1     |0x69b6|3     |0x1800|304   |1728  |0           |0           |

     PAGE DATA: (292 BYTES USED,1728 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 252 )
     [ offset   24 len   80 ]Identity                        GBase Database Server Copy
                                                             right 2001, 2021  General
                                                              Data Corporation
     [ offset  104 len    2 ]Database system state           0
     [ offset  106 len    2 ]Database system flags           0x3
     [ offset  108 len    2 ]Page Size                       2048
     [ offset  110 len    2 ]Unused                          
     [ offset  112 len    4 ]Date/Time created               2023-12-08 08:07:17
     [ offset  116 len    4 ]Version number of creator       32
     [ offset  120 len    4 ]Last modified time stamp        0
     [ offset  124 len    4 ]UID of rootdbs creator          500
     [ offset  128 len    4 ]Server Main Version             1214
     [ offset  132 len    4 ]Max Connections                 1
     [ offset  136 len    4 ]Partition Table Threshold       0
     [ offset  140 len    4 ]Index Page Logging start at     2023-12-08 08:07:17
     [ offset  144 len   32 ]SSC primary                     <NULL>

     Slot2: Slot Begin Postion ( 0 ) ,Slot Length ( 0 )

     Slot3: Slot Begin Postion ( 276 ) ,Slot Length ( 28 )

     [ offset 2032 len    4 ]Slot  3: [2B]Slot->sl_ptr= 276,[2B]Slot->sl_len=  28
     [ offset 2036 len    4 ]Slot  2: [2B]Slot->sl_ptr=   0,[2B]Slot->sl_len=   0
     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len= 252

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |    0x269b5|
     ##################################################################################
```