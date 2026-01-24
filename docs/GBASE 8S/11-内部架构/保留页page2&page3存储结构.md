page2
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:2                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |2           |1     |0x5b31|8     |0x1800|296   |1716  |0           |0           |

     PAGE DATA: (304 BYTES USED,1716 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 48 )
     [ offset   24 len    4 ]Time Stamp Of Checkpoint        0x45b1a
     [ offset   28 len    4 ]Time Of Checkpoint              2023-12-08 08:12:48
     [ offset   32 len    4 ]Physical Log Offset             263
     [ offset   36 len    4 ]Physical Log Chunk Number       1
     [ offset   40 len    4 ]Physical Log Size               25000 (p)
     [ offset   44 len    4 ]Physical Log Position At Ckpt   1032
     [ offset   48 len    4 ]Logical Log Unique Identifier   4
     [ offset   52 len    4 ]Logical Log Position At Ckpt    0x9df018
     [ offset   56 len    4 ]Checkpoint Interval             7
     [ offset   60 len    4 ]DBspace Descriptor Page         4
     [ offset   64 len    4 ]Chunk Descriptor Page           6
     [ offset   68 len    4 ]Mirror Chunk Descriptor Page    8

     Slot2: Slot Begin Postion ( 72 ) ,Slot Length ( 32 )
     [ offset   72 len    2 ]Log File Number                 1
     [ offset   74 len    2 ]Log File Flags                  0x1(U------)
     [ offset   76 len    4 ]Time Stamp                      0x15117
     [ offset   80 len    4 ]Date/Time File Filled           2023-12-08 08:07:29
     [ offset   84 len    4 ]Unique Identifier               1
     [ offset   88 len    4 ]Physical Offset                 25263
     [ offset   92 len    4 ]Physical Chunk                  1
     [ offset   96 len    4 ]Log Size                        5000 (p)
     [ offset  100 len    4 ]Number Pages Used               5000 (p)

     Slot3: Slot Begin Postion ( 104 ) ,Slot Length ( 32 )
     [ offset  104 len    2 ]Log File Number                 2
     [ offset  106 len    2 ]Log File Flags                  0x1(U------)
     [ offset  108 len    4 ]Time Stamp                      0x29ece
     [ offset  112 len    4 ]Date/Time File Filled           2023-12-08 08:07:45
     [ offset  116 len    4 ]Unique Identifier               2
     [ offset  120 len    4 ]Physical Offset                 30263
     [ offset  124 len    4 ]Physical Chunk                  1
     [ offset  128 len    4 ]Log Size                        5000 (p)
     [ offset  132 len    4 ]Number Pages Used               5000 (p)

     Slot4: Slot Begin Postion ( 136 ) ,Slot Length ( 32 )
     [ offset  136 len    2 ]Log File Number                 3
     [ offset  138 len    2 ]Log File Flags                  0x1(U------)
     [ offset  140 len    4 ]Time Stamp                      0x3b6fc
     [ offset  144 len    4 ]Date/Time File Filled           2023-12-08 08:07:48
     [ offset  148 len    4 ]Unique Identifier               3
     [ offset  152 len    4 ]Physical Offset                 35263
     [ offset  156 len    4 ]Physical Chunk                  1
     [ offset  160 len    4 ]Log Size                        5000 (p)
     [ offset  164 len    4 ]Number Pages Used               5000 (p)

     Slot5: Slot Begin Postion ( 168 ) ,Slot Length ( 32 )
     [ offset  168 len    2 ]Log File Number                 4
     [ offset  170 len    2 ]Log File Flags                  0x3(U---C-L)
     [ offset  172 len    4 ]Time Stamp                      0x0
     [ offset  176 len    4 ]Date/Time File Filled           0
     [ offset  180 len    4 ]Unique Identifier               4
     [ offset  184 len    4 ]Physical Offset                 40263
     [ offset  188 len    4 ]Physical Chunk                  1
     [ offset  192 len    4 ]Log Size                        5000 (p)
     [ offset  196 len    4 ]Number Pages Used               2528 (p)

     Slot6: Slot Begin Postion ( 200 ) ,Slot Length ( 32 )
     [ offset  200 len    2 ]Log File Number                 5
     [ offset  202 len    2 ]Log File Flags                  0x8(A------)
     [ offset  204 len    4 ]Time Stamp                      0x0
     [ offset  208 len    4 ]Date/Time File Filled           0
     [ offset  212 len    4 ]Unique Identifier               0
     [ offset  216 len    4 ]Physical Offset                 45263
     [ offset  220 len    4 ]Physical Chunk                  1
     [ offset  224 len    4 ]Log Size                        5000 (p)
     [ offset  228 len    4 ]Number Pages Used               0 (p)

     Slot7: Slot Begin Postion ( 232 ) ,Slot Length ( 32 )
     [ offset  232 len    2 ]Log File Number                 6
     [ offset  234 len    2 ]Log File Flags                  0x8(A------)
     [ offset  236 len    4 ]Time Stamp                      0x0
     [ offset  240 len    4 ]Date/Time File Filled           0
     [ offset  244 len    4 ]Unique Identifier               0
     [ offset  248 len    4 ]Physical Offset                 50263
     [ offset  252 len    4 ]Physical Chunk                  1
     [ offset  256 len    4 ]Log Size                        5000 (p)
     [ offset  260 len    4 ]Number Pages Used               0 (p)

     Slot8: Slot Begin Postion ( 264 ) ,Slot Length ( 32 )
     [ offset  264 len    2 ]Log File Number                 7
     [ offset  266 len    2 ]Log File Flags                  0x8(A------)
     [ offset  268 len    4 ]Time Stamp                      0x0
     [ offset  272 len    4 ]Date/Time File Filled           0
     [ offset  276 len    4 ]Unique Identifier               0
     [ offset  280 len    4 ]Physical Offset                 55263
     [ offset  284 len    4 ]Physical Chunk                  1
     [ offset  288 len    4 ]Log Size                        5000 (p)
     [ offset  292 len    4 ]Number Pages Used               0 (p)

     [ offset 2012 len    4 ]Slot  8: [2B]Slot->sl_ptr= 264,[2B]Slot->sl_len=  32
     [ offset 2016 len    4 ]Slot  7: [2B]Slot->sl_ptr= 232,[2B]Slot->sl_len=  32
     [ offset 2020 len    4 ]Slot  6: [2B]Slot->sl_ptr= 200,[2B]Slot->sl_len=  32
     [ offset 2024 len    4 ]Slot  5: [2B]Slot->sl_ptr= 168,[2B]Slot->sl_len=  32
     [ offset 2028 len    4 ]Slot  4: [2B]Slot->sl_ptr= 136,[2B]Slot->sl_len=  32
     [ offset 2032 len    4 ]Slot  3: [2B]Slot->sl_ptr= 104,[2B]Slot->sl_len=  32
     [ offset 2036 len    4 ]Slot  2: [2B]Slot->sl_ptr=  72,[2B]Slot->sl_len=  32
     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len=  48

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |    0x45b36|
     ##################################################################################
```
page3
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:3                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |3           |1     |0x5b45|8     |0x1800|296   |1716  |0           |0           |

     PAGE DATA: (304 BYTES USED,1716 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 48 )
     [ offset   24 len    4 ]Time Stamp Of Checkpoint        0x45b3f
     [ offset   28 len    4 ]Time Of Checkpoint              2023-12-08 08:18:53
     [ offset   32 len    4 ]Physical Log Offset             263
     [ offset   36 len    4 ]Physical Log Chunk Number       1
     [ offset   40 len    4 ]Physical Log Size               25000 (p)
     [ offset   44 len    4 ]Physical Log Position At Ckpt   1045
     [ offset   48 len    4 ]Logical Log Unique Identifier   4
     [ offset   52 len    4 ]Logical Log Position At Ckpt    0x9e1018
     [ offset   56 len    4 ]Checkpoint Interval             8
     [ offset   60 len    4 ]DBspace Descriptor Page         4
     [ offset   64 len    4 ]Chunk Descriptor Page           6
     [ offset   68 len    4 ]Mirror Chunk Descriptor Page    8

     Slot2: Slot Begin Postion ( 72 ) ,Slot Length ( 32 )
     [ offset   72 len    2 ]Log File Number                 1
     [ offset   74 len    2 ]Log File Flags                  0x1(U------)
     [ offset   76 len    4 ]Time Stamp                      0x15117
     [ offset   80 len    4 ]Date/Time File Filled           2023-12-08 08:07:29
     [ offset   84 len    4 ]Unique Identifier               1
     [ offset   88 len    4 ]Physical Offset                 25263
     [ offset   92 len    4 ]Physical Chunk                  1
     [ offset   96 len    4 ]Log Size                        5000 (p)
     [ offset  100 len    4 ]Number Pages Used               5000 (p)

     Slot3: Slot Begin Postion ( 104 ) ,Slot Length ( 32 )
     [ offset  104 len    2 ]Log File Number                 2
     [ offset  106 len    2 ]Log File Flags                  0x1(U------)
     [ offset  108 len    4 ]Time Stamp                      0x29ece
     [ offset  112 len    4 ]Date/Time File Filled           2023-12-08 08:07:45
     [ offset  116 len    4 ]Unique Identifier               2
     [ offset  120 len    4 ]Physical Offset                 30263
     [ offset  124 len    4 ]Physical Chunk                  1
     [ offset  128 len    4 ]Log Size                        5000 (p)
     [ offset  132 len    4 ]Number Pages Used               5000 (p)

     Slot4: Slot Begin Postion ( 136 ) ,Slot Length ( 32 )
     [ offset  136 len    2 ]Log File Number                 3
     [ offset  138 len    2 ]Log File Flags                  0x1(U------)
     [ offset  140 len    4 ]Time Stamp                      0x3b6fc
     [ offset  144 len    4 ]Date/Time File Filled           2023-12-08 08:07:48
     [ offset  148 len    4 ]Unique Identifier               3
     [ offset  152 len    4 ]Physical Offset                 35263
     [ offset  156 len    4 ]Physical Chunk                  1
     [ offset  160 len    4 ]Log Size                        5000 (p)
     [ offset  164 len    4 ]Number Pages Used               5000 (p)

     Slot5: Slot Begin Postion ( 168 ) ,Slot Length ( 32 )
     [ offset  168 len    2 ]Log File Number                 4
     [ offset  170 len    2 ]Log File Flags                  0x3(U---C-L)
     [ offset  172 len    4 ]Time Stamp                      0x0
     [ offset  176 len    4 ]Date/Time File Filled           0
     [ offset  180 len    4 ]Unique Identifier               4
     [ offset  184 len    4 ]Physical Offset                 40263
     [ offset  188 len    4 ]Physical Chunk                  1
     [ offset  192 len    4 ]Log Size                        5000 (p)
     [ offset  196 len    4 ]Number Pages Used               2530 (p)

     Slot6: Slot Begin Postion ( 200 ) ,Slot Length ( 32 )
     [ offset  200 len    2 ]Log File Number                 5
     [ offset  202 len    2 ]Log File Flags                  0x8(A------)
     [ offset  204 len    4 ]Time Stamp                      0x0
     [ offset  208 len    4 ]Date/Time File Filled           0
     [ offset  212 len    4 ]Unique Identifier               0
     [ offset  216 len    4 ]Physical Offset                 45263
     [ offset  220 len    4 ]Physical Chunk                  1
     [ offset  224 len    4 ]Log Size                        5000 (p)
     [ offset  228 len    4 ]Number Pages Used               0 (p)

     Slot7: Slot Begin Postion ( 232 ) ,Slot Length ( 32 )
     [ offset  232 len    2 ]Log File Number                 6
     [ offset  234 len    2 ]Log File Flags                  0x8(A------)
     [ offset  236 len    4 ]Time Stamp                      0x0
     [ offset  240 len    4 ]Date/Time File Filled           0
     [ offset  244 len    4 ]Unique Identifier               0
     [ offset  248 len    4 ]Physical Offset                 50263
     [ offset  252 len    4 ]Physical Chunk                  1
     [ offset  256 len    4 ]Log Size                        5000 (p)
     [ offset  260 len    4 ]Number Pages Used               0 (p)

     Slot8: Slot Begin Postion ( 264 ) ,Slot Length ( 32 )
     [ offset  264 len    2 ]Log File Number                 7
     [ offset  266 len    2 ]Log File Flags                  0x8(A------)
     [ offset  268 len    4 ]Time Stamp                      0x0
     [ offset  272 len    4 ]Date/Time File Filled           0
     [ offset  276 len    4 ]Unique Identifier               0
     [ offset  280 len    4 ]Physical Offset                 55263
     [ offset  284 len    4 ]Physical Chunk                  1
     [ offset  288 len    4 ]Log Size                        5000 (p)
     [ offset  292 len    4 ]Number Pages Used               0 (p)

     [ offset 2012 len    4 ]Slot  8: [2B]Slot->sl_ptr= 264,[2B]Slot->sl_len=  32
     [ offset 2016 len    4 ]Slot  7: [2B]Slot->sl_ptr= 232,[2B]Slot->sl_len=  32
     [ offset 2020 len    4 ]Slot  6: [2B]Slot->sl_ptr= 200,[2B]Slot->sl_len=  32
     [ offset 2024 len    4 ]Slot  5: [2B]Slot->sl_ptr= 168,[2B]Slot->sl_len=  32
     [ offset 2028 len    4 ]Slot  4: [2B]Slot->sl_ptr= 136,[2B]Slot->sl_len=  32
     [ offset 2032 len    4 ]Slot  3: [2B]Slot->sl_ptr= 104,[2B]Slot->sl_len=  32
     [ offset 2036 len    4 ]Slot  2: [2B]Slot->sl_ptr=  72,[2B]Slot->sl_len=  32
     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len=  48

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |    0x45b43|
     ##################################################################################
```
Log File Flags说明
```
0x00 			Free
0x01 			Log file in use
0x02 			Current log file
0x04 			Backed up
0x08 			Newly added (archive required)
0x10 			Log has been written to an archive tape
0x20 			Log is a temporary log file
```