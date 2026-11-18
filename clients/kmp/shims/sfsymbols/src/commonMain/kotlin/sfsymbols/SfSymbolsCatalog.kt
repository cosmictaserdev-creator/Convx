package com.composables.sfsymbols

/**
 * Metadata for an SF Symbol.
 */
public data class SfSymbolMetadata(
    val appleName: String,
    val pascalName: String,
    val categories: List<String>,
    val isRestricted: Boolean
)

/**
 * Complete catalog of all 7,007 SF Symbols with search and category indexing.
 *
 * Encoded as a single pipe-delimited string (appleName|pascalName|categories|isRestricted,
 * one symbol per line) and parsed lazily, since generating ~7,000 individual constructor-call
 * expressions overran the Kotlin compiler's memory limit on constrained CI builders (JitPack).
 */
public object SfSymbolsCatalog {
    private val raw: String = """
0.circle.fill|SF0CircleFill|Indices,Multicolor|0
0.circle|SF0Circle|Draw,Indices,Variable|0
0.square.fill|SF0SquareFill|Indices,Multicolor|0
0.square|SF0Square|Draw,Indices|0
00.circle.fill|SF00CircleFill|Indices,Multicolor|0
00.circle|SF00Circle|Draw,Indices,Variable|0
00.square.fill|SF00SquareFill|Indices,Multicolor|0
00.square|SF00Square|Draw,Indices|0
01.circle.fill|SF01CircleFill|Indices,Multicolor|0
01.circle|SF01Circle|Draw,Indices,Variable|0
01.square.fill|SF01SquareFill|Indices,Multicolor|0
01.square|SF01Square|Draw,Indices|0
02.circle.fill|SF02CircleFill|Indices,Multicolor|0
02.circle|SF02Circle|Draw,Indices,Variable|0
02.square.fill|SF02SquareFill|Indices,Multicolor|0
02.square|SF02Square|Draw,Indices|0
03.circle.fill|SF03CircleFill|Indices,Multicolor|0
03.circle|SF03Circle|Draw,Indices,Variable|0
03.square.fill|SF03SquareFill|Indices,Multicolor|0
03.square|SF03Square|Draw,Indices|0
04.circle.fill|SF04CircleFill|Indices,Multicolor|0
04.circle|SF04Circle|Draw,Indices,Variable|0
04.square.fill|SF04SquareFill|Indices,Multicolor|0
04.square|SF04Square|Draw,Indices|0
05.circle.fill|SF05CircleFill|Indices,Multicolor|0
05.circle|SF05Circle|Draw,Indices,Variable|0
05.square.fill|SF05SquareFill|Indices,Multicolor|0
05.square|SF05Square|Draw,Indices|0
06.circle.fill|SF06CircleFill|Indices,Multicolor|0
06.circle|SF06Circle|Draw,Indices,Variable|0
06.square.fill|SF06SquareFill|Indices,Multicolor|0
06.square|SF06Square|Draw,Indices|0
07.circle.fill|SF07CircleFill|Indices,Multicolor|0
07.circle|SF07Circle|Draw,Indices,Variable|0
07.square.fill|SF07SquareFill|Indices,Multicolor|0
07.square|SF07Square|Draw,Indices|0
08.circle.fill|SF08CircleFill|Indices,Multicolor|0
08.circle|SF08Circle|Draw,Indices,Variable|0
08.square.fill|SF08SquareFill|Indices,Multicolor|0
08.square|SF08Square|Draw,Indices|0
09.circle.fill|SF09CircleFill|Indices,Multicolor|0
09.circle|SF09Circle|Draw,Indices,Variable|0
09.square.fill|SF09SquareFill|Indices,Multicolor|0
09.square|SF09Square|Draw,Indices|0
1.brakesignal|SF1Brakesignal|Automotive|0
1.calendar|SF1Calendar|Multicolor,Objects & Tools|0
1.circle.fill|SF1CircleFill|Indices,Multicolor|0
1.circle|SF1Circle|Draw,Indices,Variable|0
1.lane|SF1Lane|Fitness|0
1.magnifyingglass|SF1Magnifyingglass|Objects & Tools|0
1.square.fill|SF1SquareFill|Indices,Multicolor|0
1.square|SF1Square|Draw,Indices|0
10.arrow.trianglehead.clockwise|SF10ArrowTriangleheadClockwise|Arrows,Draw,Media|0
10.arrow.trianglehead.counterclockwise|SF10ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
10.calendar|SF10Calendar|Multicolor,Objects & Tools|0
10.circle.fill|SF10CircleFill|Indices,Multicolor|0
10.circle|SF10Circle|Draw,Indices,Variable|0
10.lane|SF10Lane|Fitness|0
10.square.fill|SF10SquareFill|Indices,Multicolor|0
10.square|SF10Square|Draw,Indices|0
11.calendar|SF11Calendar|Multicolor,Objects & Tools|0
11.circle.fill|SF11CircleFill|Indices,Multicolor|0
11.circle|SF11Circle|Draw,Indices,Variable|0
11.lane|SF11Lane|Fitness|0
11.square.fill|SF11SquareFill|Indices,Multicolor|0
11.square|SF11Square|Draw,Indices|0
12.calendar|SF12Calendar|Multicolor,Objects & Tools|0
12.circle.fill|SF12CircleFill|Indices,Multicolor|0
12.circle|SF12Circle|Draw,Indices,Variable|0
12.lane|SF12Lane|Fitness|0
12.square.fill|SF12SquareFill|Indices,Multicolor|0
12.square|SF12Square|Draw,Indices|0
13.calendar|SF13Calendar|Multicolor,Objects & Tools|0
13.circle.fill|SF13CircleFill|Indices,Multicolor|0
13.circle|SF13Circle|Draw,Indices,Variable|0
13.square.fill|SF13SquareFill|Indices,Multicolor|0
13.square|SF13Square|Draw,Indices|0
14.calendar|SF14Calendar|Multicolor,Objects & Tools|0
14.circle.fill|SF14CircleFill|Indices,Multicolor|0
14.circle|SF14Circle|Draw,Indices,Variable|0
14.square.fill|SF14SquareFill|Indices,Multicolor|0
14.square|SF14Square|Draw,Indices|0
15.arrow.trianglehead.clockwise|SF15ArrowTriangleheadClockwise|Arrows,Draw,Media|0
15.arrow.trianglehead.counterclockwise|SF15ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
15.calendar|SF15Calendar|Multicolor,Objects & Tools|0
15.circle.fill|SF15CircleFill|Indices,Multicolor|0
15.circle|SF15Circle|Draw,Indices,Variable|0
15.square.fill|SF15SquareFill|Indices,Multicolor|0
15.square|SF15Square|Draw,Indices|0
16.calendar|SF16Calendar|Multicolor,Objects & Tools|0
16.circle.fill|SF16CircleFill|Indices,Multicolor|0
16.circle|SF16Circle|Draw,Indices,Variable|0
16.square.fill|SF16SquareFill|Indices,Multicolor|0
16.square|SF16Square|Draw,Indices|0
17.calendar|SF17Calendar|Multicolor,Objects & Tools|0
17.circle.fill|SF17CircleFill|Indices,Multicolor|0
17.circle|SF17Circle|Draw,Indices,Variable|0
17.square.fill|SF17SquareFill|Indices,Multicolor|0
17.square|SF17Square|Draw,Indices|0
18.calendar|SF18Calendar|Multicolor,Objects & Tools|0
18.circle.fill|SF18CircleFill|Indices,Multicolor|0
18.circle|SF18Circle|Draw,Indices,Variable|0
18.square.fill|SF18SquareFill|Indices,Multicolor|0
18.square|SF18Square|Draw,Indices|0
19.calendar|SF19Calendar|Multicolor,Objects & Tools|0
19.circle.fill|SF19CircleFill|Indices,Multicolor|0
19.circle|SF19Circle|Draw,Indices,Variable|0
19.square.fill|SF19SquareFill|Indices,Multicolor|0
19.square|SF19Square|Draw,Indices|0
2.brakesignal|SF2Brakesignal|Automotive|0
2.calendar|SF2Calendar|Multicolor,Objects & Tools|0
2.circle.fill|SF2CircleFill|Indices,Multicolor|0
2.circle|SF2Circle|Draw,Indices,Variable|0
2.lane|SF2Lane|Fitness|0
2.square.fill|SF2SquareFill|Indices,Multicolor|0
2.square|SF2Square|Draw,Indices|0
20.calendar|SF20Calendar|Multicolor,Objects & Tools|0
20.circle.fill|SF20CircleFill|Indices,Multicolor|0
20.circle|SF20Circle|Draw,Indices,Variable|0
20.square.fill|SF20SquareFill|Indices,Multicolor|0
20.square|SF20Square|Draw,Indices|0
21.calendar|SF21Calendar|Multicolor,Objects & Tools|0
21.circle.fill|SF21CircleFill|Indices,Multicolor|0
21.circle|SF21Circle|Draw,Indices,Variable|0
21.square.fill|SF21SquareFill|Indices,Multicolor|0
21.square|SF21Square|Draw,Indices|0
22.calendar|SF22Calendar|Multicolor,Objects & Tools|0
22.circle.fill|SF22CircleFill|Indices,Multicolor|0
22.circle|SF22Circle|Draw,Indices,Variable|0
22.square.fill|SF22SquareFill|Indices,Multicolor|0
22.square|SF22Square|Draw,Indices|0
23.calendar|SF23Calendar|Multicolor,Objects & Tools|0
23.circle.fill|SF23CircleFill|Indices,Multicolor|0
23.circle|SF23Circle|Draw,Indices,Variable|0
23.square.fill|SF23SquareFill|Indices,Multicolor|0
23.square|SF23Square|Draw,Indices|0
24.calendar|SF24Calendar|Multicolor,Objects & Tools|0
24.circle.fill|SF24CircleFill|Indices,Multicolor|0
24.circle|SF24Circle|Draw,Indices,Variable|0
24.square.fill|SF24SquareFill|Indices,Multicolor|0
24.square|SF24Square|Draw,Indices|0
25.calendar|SF25Calendar|Multicolor,Objects & Tools|0
25.circle.fill|SF25CircleFill|Indices,Multicolor|0
25.circle|SF25Circle|Draw,Indices,Variable|0
25.square.fill|SF25SquareFill|Indices,Multicolor|0
25.square|SF25Square|Draw,Indices|0
26.calendar|SF26Calendar|Multicolor,Objects & Tools|0
26.circle.fill|SF26CircleFill|Indices,Multicolor|0
26.circle|SF26Circle|Draw,Indices,Variable|0
26.square.fill|SF26SquareFill|Indices,Multicolor|0
26.square|SF26Square|Draw,Indices|0
27.calendar|SF27Calendar|Multicolor,Objects & Tools|0
27.circle.fill|SF27CircleFill|Indices,Multicolor|0
27.circle|SF27Circle|Draw,Indices,Variable|0
27.square.fill|SF27SquareFill|Indices,Multicolor|0
27.square|SF27Square|Draw,Indices|0
28.calendar|SF28Calendar|Multicolor,Objects & Tools|0
28.circle.fill|SF28CircleFill|Indices,Multicolor|0
28.circle|SF28Circle|Draw,Indices,Variable|0
28.square.fill|SF28SquareFill|Indices,Multicolor|0
28.square|SF28Square|Draw,Indices|0
29.calendar|SF29Calendar|Multicolor,Objects & Tools|0
29.circle.fill|SF29CircleFill|Indices,Multicolor|0
29.circle|SF29Circle|Draw,Indices,Variable|0
29.square.fill|SF29SquareFill|Indices,Multicolor|0
29.square|SF29Square|Draw,Indices|0
2h.circle.fill|SF2hCircleFill|Automotive,Multicolor|0
2h.circle|SF2hCircle|Automotive,Draw,Variable|0
2h|SF2h|Automotive|0
3.calendar|SF3Calendar|Multicolor,Objects & Tools|0
3.circle.fill|SF3CircleFill|Indices,Multicolor|0
3.circle|SF3Circle|Draw,Indices,Variable|0
3.lane|SF3Lane|Fitness|0
3.square.fill|SF3SquareFill|Indices,Multicolor|0
3.square|SF3Square|Draw,Indices|0
30.arrow.trianglehead.clockwise|SF30ArrowTriangleheadClockwise|Arrows,Draw,Media|0
30.arrow.trianglehead.counterclockwise|SF30ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
30.calendar|SF30Calendar|Multicolor,Objects & Tools|0
30.circle.fill|SF30CircleFill|Indices,Multicolor|0
30.circle|SF30Circle|Draw,Indices,Variable|0
30.square.fill|SF30SquareFill|Indices,Multicolor|0
30.square|SF30Square|Draw,Indices|0
31.calendar|SF31Calendar|Multicolor,Objects & Tools|0
31.circle.fill|SF31CircleFill|Indices,Multicolor|0
31.circle|SF31Circle|Draw,Indices,Variable|0
31.square.fill|SF31SquareFill|Indices,Multicolor|0
31.square|SF31Square|Draw,Indices|0
32.circle.fill|SF32CircleFill|Indices,Multicolor|0
32.circle|SF32Circle|Draw,Indices,Variable|0
32.square.fill|SF32SquareFill|Indices,Multicolor|0
32.square|SF32Square|Draw,Indices|0
33.circle.fill|SF33CircleFill|Indices,Multicolor|0
33.circle|SF33Circle|Draw,Indices,Variable|0
33.square.fill|SF33SquareFill|Indices,Multicolor|0
33.square|SF33Square|Draw,Indices|0
34.circle.fill|SF34CircleFill|Indices,Multicolor|0
34.circle|SF34Circle|Draw,Indices,Variable|0
34.square.fill|SF34SquareFill|Indices,Multicolor|0
34.square|SF34Square|Draw,Indices|0
35.circle.fill|SF35CircleFill|Indices,Multicolor|0
35.circle|SF35Circle|Draw,Indices,Variable|0
35.square.fill|SF35SquareFill|Indices,Multicolor|0
35.square|SF35Square|Draw,Indices|0
36.circle.fill|SF36CircleFill|Indices,Multicolor|0
36.circle|SF36Circle|Draw,Indices,Variable|0
36.square.fill|SF36SquareFill|Indices,Multicolor|0
36.square|SF36Square|Draw,Indices|0
37.circle.fill|SF37CircleFill|Indices,Multicolor|0
37.circle|SF37Circle|Draw,Indices,Variable|0
37.square.fill|SF37SquareFill|Indices,Multicolor|0
37.square|SF37Square|Draw,Indices|0
38.circle.fill|SF38CircleFill|Indices,Multicolor|0
38.circle|SF38Circle|Draw,Indices,Variable|0
38.square.fill|SF38SquareFill|Indices,Multicolor|0
38.square|SF38Square|Draw,Indices|0
39.circle.fill|SF39CircleFill|Indices,Multicolor|0
39.circle|SF39Circle|Draw,Indices,Variable|0
39.square.fill|SF39SquareFill|Indices,Multicolor|0
39.square|SF39Square|Draw,Indices|0
4.alt.circle.fill|SF4AltCircleFill|Indices,Multicolor|0
4.alt.circle|SF4AltCircle|Draw,Indices,Variable|0
4.alt.square.fill|SF4AltSquareFill|Indices,Multicolor|0
4.alt.square|SF4AltSquare|Draw,Indices|0
4.calendar|SF4Calendar|Multicolor,Objects & Tools|0
4.circle.fill|SF4CircleFill|Indices,Multicolor|0
4.circle|SF4Circle|Draw,Indices,Variable|0
4.lane|SF4Lane|Fitness|0
4.square.fill|SF4SquareFill|Indices,Multicolor|0
4.square|SF4Square|Draw,Indices|0
40.circle.fill|SF40CircleFill|Indices,Multicolor|0
40.circle|SF40Circle|Draw,Indices,Variable|0
40.square.fill|SF40SquareFill|Indices,Multicolor|0
40.square|SF40Square|Draw,Indices|0
41.circle.fill|SF41CircleFill|Indices,Multicolor|0
41.circle|SF41Circle|Draw,Indices,Variable|0
41.square.fill|SF41SquareFill|Indices,Multicolor|0
41.square|SF41Square|Draw,Indices|0
42.circle.fill|SF42CircleFill|Indices,Multicolor|0
42.circle|SF42Circle|Draw,Indices,Variable|0
42.square.fill|SF42SquareFill|Indices,Multicolor|0
42.square|SF42Square|Draw,Indices|0
43.circle.fill|SF43CircleFill|Indices,Multicolor|0
43.circle|SF43Circle|Draw,Indices,Variable|0
43.square.fill|SF43SquareFill|Indices,Multicolor|0
43.square|SF43Square|Draw,Indices|0
44.circle.fill|SF44CircleFill|Indices,Multicolor|0
44.circle|SF44Circle|Draw,Indices,Variable|0
44.square.fill|SF44SquareFill|Indices,Multicolor|0
44.square|SF44Square|Draw,Indices|0
45.arrow.trianglehead.clockwise|SF45ArrowTriangleheadClockwise|Arrows,Draw,Media|0
45.arrow.trianglehead.counterclockwise|SF45ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
45.circle.fill|SF45CircleFill|Indices,Multicolor|0
45.circle|SF45Circle|Draw,Indices,Variable|0
45.square.fill|SF45SquareFill|Indices,Multicolor|0
45.square|SF45Square|Draw,Indices|0
46.circle.fill|SF46CircleFill|Indices,Multicolor|0
46.circle|SF46Circle|Draw,Indices,Variable|0
46.square.fill|SF46SquareFill|Indices,Multicolor|0
46.square|SF46Square|Draw,Indices|0
47.circle.fill|SF47CircleFill|Indices,Multicolor|0
47.circle|SF47Circle|Draw,Indices,Variable|0
47.square.fill|SF47SquareFill|Indices,Multicolor|0
47.square|SF47Square|Draw,Indices|0
48.circle.fill|SF48CircleFill|Indices,Multicolor|0
48.circle|SF48Circle|Draw,Indices,Variable|0
48.square.fill|SF48SquareFill|Indices,Multicolor|0
48.square|SF48Square|Draw,Indices|0
49.circle.fill|SF49CircleFill|Indices,Multicolor|0
49.circle|SF49Circle|Draw,Indices,Variable|0
49.square.fill|SF49SquareFill|Indices,Multicolor|0
49.square|SF49Square|Draw,Indices|0
4a.circle.fill|SF4aCircleFill|Automotive,Multicolor|0
4a.circle|SF4aCircle|Automotive,Draw,Variable|0
4a|SF4a|Automotive|0
4h.circle.fill|SF4hCircleFill|Automotive,Multicolor|0
4h.circle|SF4hCircle|Automotive,Draw,Variable|0
4h|SF4h|Automotive|0
4k.tv.fill|SF4kTvFill|Devices|0
4k.tv|SF4kTv|Devices|0
4l.circle.fill|SF4lCircleFill|Automotive,Multicolor|0
4l.circle|SF4lCircle|Automotive,Draw,Variable|0
4l|SF4l|Automotive|0
5.arrow.trianglehead.clockwise|SF5ArrowTriangleheadClockwise|Arrows,Draw,Media|0
5.arrow.trianglehead.counterclockwise|SF5ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
5.calendar|SF5Calendar|Multicolor,Objects & Tools|0
5.circle.fill|SF5CircleFill|Indices,Multicolor|0
5.circle|SF5Circle|Draw,Indices,Variable|0
5.lane|SF5Lane|Fitness|0
5.square.fill|SF5SquareFill|Indices,Multicolor|0
5.square|SF5Square|Draw,Indices|0
50.circle.fill|SF50CircleFill|Indices,Multicolor|0
50.circle|SF50Circle|Draw,Indices,Variable|0
50.square.fill|SF50SquareFill|Indices,Multicolor|0
50.square|SF50Square|Draw,Indices|0
6.alt.circle.fill|SF6AltCircleFill|Indices,Multicolor|0
6.alt.circle|SF6AltCircle|Draw,Indices,Variable|0
6.alt.square.fill|SF6AltSquareFill|Indices,Multicolor|0
6.alt.square|SF6AltSquare|Draw,Indices|0
6.calendar|SF6Calendar|Multicolor,Objects & Tools|0
6.circle.fill|SF6CircleFill|Indices,Multicolor|0
6.circle|SF6Circle|Draw,Indices,Variable|0
6.lane|SF6Lane|Fitness|0
6.square.fill|SF6SquareFill|Indices,Multicolor|0
6.square|SF6Square|Draw,Indices|0
60.arrow.trianglehead.clockwise|SF60ArrowTriangleheadClockwise|Arrows,Draw,Media|0
60.arrow.trianglehead.counterclockwise|SF60ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
7.calendar|SF7Calendar|Multicolor,Objects & Tools|0
7.circle.fill|SF7CircleFill|Indices,Multicolor|0
7.circle|SF7Circle|Draw,Indices,Variable|0
7.lane|SF7Lane|Fitness|0
7.square.fill|SF7SquareFill|Indices,Multicolor|0
7.square|SF7Square|Draw,Indices|0
75.arrow.trianglehead.clockwise|SF75ArrowTriangleheadClockwise|Arrows,Draw,Media|0
75.arrow.trianglehead.counterclockwise|SF75ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
8.calendar|SF8Calendar|Multicolor,Objects & Tools|0
8.circle.fill|SF8CircleFill|Indices,Multicolor|0
8.circle|SF8Circle|Draw,Indices,Variable|0
8.lane|SF8Lane|Fitness|0
8.square.fill|SF8SquareFill|Indices,Multicolor|0
8.square|SF8Square|Draw,Indices|0
9.alt.circle.fill|SF9AltCircleFill|Indices,Multicolor|0
9.alt.circle|SF9AltCircle|Draw,Indices,Variable|0
9.alt.square.fill|SF9AltSquareFill|Indices,Multicolor|0
9.alt.square|SF9AltSquare|Draw,Indices|0
9.calendar|SF9Calendar|Multicolor,Objects & Tools|0
9.circle.fill|SF9CircleFill|Indices,Multicolor|0
9.circle|SF9Circle|Draw,Indices,Variable|0
9.lane|SF9Lane|Fitness|0
9.square.fill|SF9SquareFill|Indices,Multicolor|0
9.square|SF9Square|Draw,Indices|0
90.arrow.trianglehead.clockwise|SF90ArrowTriangleheadClockwise|Arrows,Draw,Media|0
90.arrow.trianglehead.counterclockwise|SF90ArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
a.circle.fill|SFACircleFill|Gaming,Indices,Multicolor|0
a.circle|SFACircle|Draw,Gaming,Indices,Variable|0
a.square.fill|SFASquareFill|Indices,Multicolor|0
a.square|SFASquare|Draw,Indices|0
abs.brakesignal.slash|SFAbsBrakesignalSlash|Automotive,Multicolor|0
abs.brakesignal|SFAbsBrakesignal|Automotive,Multicolor|0
abs.circle.fill|SFAbsCircleFill|Automotive,Multicolor|0
abs.circle|SFAbsCircle|Automotive,Draw,Multicolor,Variable|0
abs|SFAbs|Automotive,Multicolor|0
accessibility.badge.arrow.up.right|SFAccessibilityBadgeArrowUpRight|Accessibility,Human,Multicolor|1
accessibility.fill|SFAccessibilityFill|Accessibility,Human,Multicolor|1
accessibility|SFAccessibility|Accessibility,Draw,Human,Multicolor,Variable|1
air.car.side.fill|SFAirCarSideFill|Automotive,Multicolor|0
air.car.side|SFAirCarSide|Automotive|0
air.conditioner.horizontal.fill|SFAirConditionerHorizontalFill|Home,Objects & Tools|0
air.conditioner.horizontal|SFAirConditionerHorizontal|Home,Objects & Tools|0
air.conditioner.slash|SFAirConditionerSlash|Automotive,Draw|0
air.conditioner|SFAirConditioner|Automotive|0
air.conditioner.vertical.fill|SFAirConditionerVerticalFill|Home,Objects & Tools|0
air.conditioner.vertical|SFAirConditionerVertical|Home,Objects & Tools|0
air.convertible.side.fill|SFAirConvertibleSideFill|Automotive,Multicolor|0
air.convertible.side|SFAirConvertibleSide|Automotive|0
air.pickup.side.fill|SFAirPickupSideFill|Automotive,Multicolor|0
air.pickup.side|SFAirPickupSide|Automotive|0
air.purifier.fill|SFAirPurifierFill|Home,Objects & Tools|0
air.purifier|SFAirPurifier|Home,Objects & Tools|0
air.suv.side.fill|SFAirSuvSideFill|Automotive,Multicolor|0
air.suv.side|SFAirSuvSide|Automotive|0
airplane.arrival|SFAirplaneArrival|Draw,Transportation|0
airplane.circle.fill|SFAirplaneCircleFill|Multicolor,Transportation|0
airplane.circle|SFAirplaneCircle|Draw,Multicolor,Transportation,Variable|0
airplane.cloud|SFAirplaneCloud|Transportation|0
airplane.departure|SFAirplaneDeparture|Draw,Transportation|0
airplane.landed|SFAirplaneLanded|Draw,Transportation|0
airplane.path.dotted|SFAirplanePathDotted|Transportation|0
airplane|SFAirplane|Multicolor,Transportation|0
airplane.ticket.fill|SFAirplaneTicketFill|Transportation|0
airplane.ticket|SFAirplaneTicket|Transportation|0
airplane.up.forward.app.fill|SFAirplaneUpForwardAppFill|Multicolor,Transportation|0
airplane.up.forward.app|SFAirplaneUpForwardApp|Transportation|0
airplane.up.forward|SFAirplaneUpForward|Transportation|0
airplane.up.right.app.fill|SFAirplaneUpRightAppFill|Multicolor,Transportation|0
airplane.up.right.app|SFAirplaneUpRightApp|Transportation|0
airplane.up.right|SFAirplaneUpRight|Transportation|0
airplaneseat|SFAirplaneseat|Transportation|0
airplay.audio.badge.exclamationmark|SFAirplayAudioBadgeExclamationmark|Multicolor,Variable|1
airplay.audio.circle.fill|SFAirplayAudioCircleFill|Multicolor,Variable|1
airplay.audio.circle|SFAirplayAudioCircle|Draw,Variable|1
airplay.audio|SFAirplayAudio|Draw,Variable|1
airplay.video.badge.exclamationmark|SFAirplayVideoBadgeExclamationmark|Multicolor|1
airplay.video.circle.fill|SFAirplayVideoCircleFill|Multicolor|1
airplay.video.circle|SFAirplayVideoCircle|Draw,Variable|1
airplay.video|SFAirplayVideo||1
airpod.gen3.left|SFAirpodGen3Left|Devices|1
airpod.gen3.right|SFAirpodGen3Right|Devices|1
airpod.left|SFAirpodLeft|Devices|1
airpod.right|SFAirpodRight|Devices|1
airpods.chargingcase.fill|SFAirpodsChargingcaseFill|Devices|1
airpods.chargingcase|SFAirpodsChargingcase|Devices|1
airpods.chargingcase.wireless.fill|SFAirpodsChargingcaseWirelessFill|Devices|1
airpods.chargingcase.wireless|SFAirpodsChargingcaseWireless|Devices|1
airpods.gen3.chargingcase.wireless.fill|SFAirpodsGen3ChargingcaseWirelessFill|Devices|1
airpods.gen3.chargingcase.wireless|SFAirpodsGen3ChargingcaseWireless|Devices|1
airpods.gen3|SFAirpodsGen3|Devices|1
airpods.gen4.chargingcase.wireless.fill|SFAirpodsGen4ChargingcaseWirelessFill|Devices|1
airpods.gen4.chargingcase.wireless|SFAirpodsGen4ChargingcaseWireless|Devices|1
airpods.gen4.left|SFAirpodsGen4Left|Devices|1
airpods.gen4.right|SFAirpodsGen4Right|Devices|1
airpods.gen4|SFAirpodsGen4|Devices|1
airpods.max|SFAirpodsMax|Devices,Objects & Tools|1
airpods.pro.chargingcase.wireless.fill|SFAirpodsProChargingcaseWirelessFill|Devices|1
airpods.pro.chargingcase.wireless.radiowaves.left.and.right.fill|SFAirpodsProChargingcaseWirelessRadiowavesLeftAndRightFill|Devices,Draw,Variable|1
airpods.pro.chargingcase.wireless.radiowaves.left.and.right|SFAirpodsProChargingcaseWirelessRadiowavesLeftAndRight|Devices,Draw,Variable|1
airpods.pro.chargingcase.wireless|SFAirpodsProChargingcaseWireless|Devices|1
airpods.pro.left|SFAirpodsProLeft|Devices|1
airpods.pro.right|SFAirpodsProRight|Devices|1
airpods.pro|SFAirpodsPro|Devices|1
airpods|SFAirpods|Devices|1
airport.express|SFAirportExpress|Devices|1
airport.extreme|SFAirportExtreme|Devices|1
airport.extreme.tower|SFAirportExtremeTower|Devices|1
airtag.fill|SFAirtagFill|Devices|1
airtag.radiowaves.forward.fill|SFAirtagRadiowavesForwardFill|Devices,Draw,Variable|1
airtag.radiowaves.forward|SFAirtagRadiowavesForward|Devices,Draw,Variable|1
airtag|SFAirtag|Devices|1
alarm.fill|SFAlarmFill|Objects & Tools,Time|0
alarm|SFAlarm|Multicolor,Objects & Tools,Time|0
alarm.waves.left.and.right.fill|SFAlarmWavesLeftAndRightFill|Draw,Objects & Tools,Time,Variable|0
alarm.waves.left.and.right|SFAlarmWavesLeftAndRight|Draw,Objects & Tools,Time,Variable|0
align.horizontal.center.fill|SFAlignHorizontalCenterFill|Editing|0
align.horizontal.center|SFAlignHorizontalCenter|Editing|0
align.horizontal.left.fill|SFAlignHorizontalLeftFill|Draw,Editing|0
align.horizontal.left|SFAlignHorizontalLeft|Draw,Editing|0
align.horizontal.right.fill|SFAlignHorizontalRightFill|Draw,Editing|0
align.horizontal.right|SFAlignHorizontalRight|Draw,Editing|0
align.vertical.bottom.fill|SFAlignVerticalBottomFill|Draw,Editing|0
align.vertical.bottom|SFAlignVerticalBottom|Draw,Editing|0
align.vertical.center.fill|SFAlignVerticalCenterFill|Editing|0
align.vertical.center|SFAlignVerticalCenter|Editing|0
align.vertical.top.fill|SFAlignVerticalTopFill|Draw,Editing|0
align.vertical.top|SFAlignVerticalTop|Draw,Editing|0
allergens.fill|SFAllergensFill|Health,Nature|0
allergens|SFAllergens|Health,Multicolor,Nature|0
alt|SFAlt|Draw,Keyboard|0
alternatingcurrent|SFAlternatingcurrent|Draw|0
american.football.circle.fill|SFAmericanFootballCircleFill|Fitness,Multicolor,Objects & Tools|0
american.football.circle|SFAmericanFootballCircle|Draw,Fitness,Objects & Tools,Variable|0
american.football.fill|SFAmericanFootballFill|Fitness,Objects & Tools|0
american.football.professional.circle.fill|SFAmericanFootballProfessionalCircleFill|Fitness,Multicolor,Objects & Tools|0
american.football.professional.circle|SFAmericanFootballProfessionalCircle|Draw,Fitness,Objects & Tools,Variable|0
american.football.professional.fill|SFAmericanFootballProfessionalFill|Fitness,Objects & Tools|0
american.football.professional|SFAmericanFootballProfessional|Fitness,Objects & Tools|0
american.football|SFAmericanFootball|Fitness,Objects & Tools|0
amplifier|SFAmplifier|Objects & Tools|0
angle|SFAngle|Math|0
ant.circle.fill|SFAntCircleFill|Multicolor,Nature|0
ant.circle|SFAntCircle|Draw,Nature,Variable|0
ant.fill|SFAntFill|Nature|0
ant|SFAnt|Nature|0
antenna.radiowaves.left.and.right.circle.fill|SFAntennaRadiowavesLeftAndRightCircleFill|Connectivity,Draw,Multicolor,Objects & Tools,Variable|0
antenna.radiowaves.left.and.right.circle|SFAntennaRadiowavesLeftAndRightCircle|Connectivity,Draw,Multicolor,Objects & Tools,Variable|0
antenna.radiowaves.left.and.right.slash.circle.fill|SFAntennaRadiowavesLeftAndRightSlashCircleFill|Connectivity,Multicolor,Objects & Tools|0
antenna.radiowaves.left.and.right.slash.circle|SFAntennaRadiowavesLeftAndRightSlashCircle|Connectivity,Draw,Objects & Tools,Variable|0
antenna.radiowaves.left.and.right.slash|SFAntennaRadiowavesLeftAndRightSlash|Connectivity,Draw,Multicolor,Objects & Tools|0
antenna.radiowaves.left.and.right|SFAntennaRadiowavesLeftAndRight|Connectivity,Draw,Multicolor,Objects & Tools,Variable|0
app.background.dotted|SFAppBackgroundDotted||0
app.badge.checkmark.fill|SFAppBadgeCheckmarkFill|Multicolor|0
app.badge.checkmark|SFAppBadgeCheckmark|Multicolor|0
app.badge.clock.fill|SFAppBadgeClockFill|Multicolor|0
app.badge.clock|SFAppBadgeClock|Multicolor|0
app.badge.fill|SFAppBadgeFill|Multicolor|0
app.badge|SFAppBadge|Multicolor|0
app.connected.to.app.below.fill|SFAppConnectedToAppBelowFill|Draw,Maps|0
app.dashed|SFAppDashed||0
app.fill|SFAppFill|Shapes|0
app.gift.fill|SFAppGiftFill||0
app.gift|SFAppGift||0
app.grid|SFAppGrid||0
app.shadow|SFAppShadow||0
app.specular|SFAppSpecular||0
app|SFApp|Shapes|0
app.translucent|SFAppTranslucent||0
appclip|SFAppclip||1
append.page.fill|SFAppendPageFill||0
append.page|SFAppendPage||0
apple.books.pages.fill|SFAppleBooksPagesFill|Multicolor|0
apple.books.pages|SFAppleBooksPages||0
apple.classical.pages.fill|SFAppleClassicalPagesFill|Multicolor|0
apple.classical.pages|SFAppleClassicalPages||0
apple.haptics.and.exclamationmark.triangle|SFAppleHapticsAndExclamationmarkTriangle|Multicolor|1
apple.haptics.and.music.note.slash|SFAppleHapticsAndMusicNoteSlash||1
apple.haptics.and.music.note|SFAppleHapticsAndMusicNote||1
apple.homekit|SFAppleHomekit|Home,Multicolor,Variable|1
apple.image.playground.fill|SFAppleImagePlaygroundFill|Multicolor|1
apple.image.playground|SFAppleImagePlayground||1
apple.intelligence.badge.xmark|SFAppleIntelligenceBadgeXmark|Multicolor|1
apple.intelligence|SFAppleIntelligence|Multicolor|1
apple.logo|SFAppleLogo||1
apple.meditate.circle.fill|SFAppleMeditateCircleFill|Health,Multicolor,Nature|1
apple.meditate.circle|SFAppleMeditateCircle|Draw,Health,Nature,Variable|1
apple.meditate.square.stack.fill|SFAppleMeditateSquareStackFill|Health,Nature|1
apple.meditate.square.stack|SFAppleMeditateSquareStack|Health,Nature|1
apple.meditate|SFAppleMeditate|Health,Nature|1
apple.podcasts.pages.fill|SFApplePodcastsPagesFill|Multicolor|0
apple.podcasts.pages|SFApplePodcastsPages||0
apple.terminal.circle.fill|SFAppleTerminalCircleFill|Multicolor|0
apple.terminal.circle|SFAppleTerminalCircle|Draw,Variable|0
apple.terminal.fill|SFAppleTerminalFill|Multicolor|0
apple.terminal.on.rectangle.fill|SFAppleTerminalOnRectangleFill|Multicolor|0
apple.terminal.on.rectangle|SFAppleTerminalOnRectangle|Multicolor|0
apple.terminal|SFAppleTerminal|Multicolor|0
apple.writing.tools|SFAppleWritingTools|Multicolor|1
applepencil.adapter.usb.c.fill|SFApplepencilAdapterUsbCFill|Devices|1
applepencil.adapter.usb.c|SFApplepencilAdapterUsbC|Devices|1
applepencil.and.scribble|SFApplepencilAndScribble|Devices|1
applepencil.doubletap|SFApplepencilDoubletap|Devices,Variable|1
applepencil.gen1|SFApplepencilGen1|Devices|1
applepencil.gen2|SFApplepencilGen2|Devices|1
applepencil.hover|SFApplepencilHover|Devices|1
applepencil.squeeze|SFApplepencilSqueeze|Devices,Variable|1
applepencil|SFApplepencil|Devices|1
applepencil.tip|SFApplepencilTip|Devices|1
applescript.fill|SFApplescriptFill||1
applescript|SFApplescript||1
appletv.badge.checkmark.fill|SFAppletvBadgeCheckmarkFill|Devices,Multicolor|1
appletv.badge.checkmark|SFAppletvBadgeCheckmark|Devices,Multicolor|1
appletv.badge.exclamationmark.fill|SFAppletvBadgeExclamationmarkFill|Devices,Multicolor|1
appletv.badge.exclamationmark|SFAppletvBadgeExclamationmark|Devices,Multicolor|1
appletv.fill|SFAppletvFill|Devices,Multicolor|1
appletv|SFAppletv|Devices|1
appletvremote.gen1.fill|SFAppletvremoteGen1Fill|Devices|1
appletvremote.gen1|SFAppletvremoteGen1|Devices|1
appletvremote.gen2.fill|SFAppletvremoteGen2Fill|Devices|1
appletvremote.gen2|SFAppletvremoteGen2|Devices|1
appletvremote.gen3.fill|SFAppletvremoteGen3Fill|Devices|1
appletvremote.gen3|SFAppletvremoteGen3|Devices|1
appletvremote.gen4.fill|SFAppletvremoteGen4Fill|Devices|1
appletvremote.gen4|SFAppletvremoteGen4|Devices|1
applewatch.and.arrow.forward|SFApplewatchAndArrowForward|Devices,Draw|1
applewatch.badge.checkmark|SFApplewatchBadgeCheckmark|Devices,Multicolor|1
applewatch.badge.exclamationmark|SFApplewatchBadgeExclamationmark|Devices,Multicolor|1
applewatch.case.sizes|SFApplewatchCaseSizes|Devices|1
applewatch.radiowaves.left.and.right|SFApplewatchRadiowavesLeftAndRight|Devices,Draw,Variable|1
applewatch.side.right|SFApplewatchSideRight|Devices|1
applewatch.slash|SFApplewatchSlash|Devices|1
applewatch|SFApplewatch|Devices|1
applewatch.watchface|SFApplewatchWatchface|Devices|1
apps.ipad.badge.checkmark|SFAppsIpadBadgeCheckmark|Devices,Multicolor|1
apps.ipad.badge.plus|SFAppsIpadBadgePlus|Devices,Multicolor|1
apps.ipad.landscape|SFAppsIpadLandscape|Devices|1
apps.ipad.on.rectangle.portrait.dashed|SFAppsIpadOnRectanglePortraitDashed|Devices|1
apps.ipad|SFAppsIpad|Devices|1
apps.iphone.badge.checkmark|SFAppsIphoneBadgeCheckmark|Devices,Multicolor|1
apps.iphone.badge.plus|SFAppsIphoneBadgePlus|Devices,Multicolor|1
apps.iphone.landscape|SFAppsIphoneLandscape|Devices|1
apps.iphone|SFAppsIphone|Devices|1
appwindow.swipe.rectangle|SFAppwindowSwipeRectangle||0
aqi.high|SFAqiHigh|Multicolor,Variable,Weather|0
aqi.low|SFAqiLow|Variable,Weather|0
aqi.medium.gauge.open|SFAqiMediumGaugeOpen|Draw,Variable|0
aqi.medium|SFAqiMedium|Variable,Weather|0
arcade.stick.and.arrow.down|SFArcadeStickAndArrowDown|Gaming|0
arcade.stick.and.arrow.left.and.arrow.right.outward|SFArcadeStickAndArrowLeftAndArrowRightOutward|Gaming|0
arcade.stick.and.arrow.left|SFArcadeStickAndArrowLeft|Gaming|0
arcade.stick.and.arrow.right|SFArcadeStickAndArrowRight|Gaming|0
arcade.stick.and.arrow.up.and.arrow.down|SFArcadeStickAndArrowUpAndArrowDown|Gaming|0
arcade.stick.and.arrow.up|SFArcadeStickAndArrowUp|Gaming|0
arcade.stick.console.fill|SFArcadeStickConsoleFill|Devices,Gaming,Objects & Tools|0
arcade.stick.console|SFArcadeStickConsole|Devices,Gaming,Objects & Tools|0
arcade.stick|SFArcadeStick|Gaming|0
archivebox.circle.fill|SFArchiveboxCircleFill|Multicolor,Objects & Tools|0
archivebox.circle|SFArchiveboxCircle|Draw,Objects & Tools,Variable|0
archivebox.fill|SFArchiveboxFill|Objects & Tools|0
archivebox|SFArchivebox|Objects & Tools|0
arkit.badge.xmark|SFArkitBadgeXmark|Multicolor|1
arkit|SFArkit||1
arrow.2.squarepath|SFArrow2Squarepath|Arrows|0
arrow.3.trianglepath|SFArrow3Trianglepath|Arrows|0
arrow.backward.circle.dotted|SFArrowBackwardCircleDotted|Arrows,Draw|0
arrow.backward.circle.fill|SFArrowBackwardCircleFill|Arrows,Draw,Multicolor|0
arrow.backward.circle|SFArrowBackwardCircle|Arrows,Draw,Variable|0
arrow.backward.square.fill|SFArrowBackwardSquareFill|Arrows,Draw,Multicolor|0
arrow.backward.square|SFArrowBackwardSquare|Arrows,Draw|0
arrow.backward|SFArrowBackward|Arrows,Draw|0
arrow.backward.to.line.circle.fill|SFArrowBackwardToLineCircleFill|Arrows,Multicolor|0
arrow.backward.to.line.circle|SFArrowBackwardToLineCircle|Arrows,Draw,Variable|0
arrow.backward.to.line.compact|SFArrowBackwardToLineCompact|Arrows,Draw,Keyboard|0
arrow.backward.to.line.square.fill|SFArrowBackwardToLineSquareFill|Arrows,Multicolor|0
arrow.backward.to.line.square|SFArrowBackwardToLineSquare|Arrows,Draw|0
arrow.backward.to.line|SFArrowBackwardToLine|Arrows,Draw,Keyboard|0
arrow.clockwise.circle.fill|SFArrowClockwiseCircleFill|Arrows,Draw,Multicolor|0
arrow.clockwise.circle|SFArrowClockwiseCircle|Arrows,Draw,Variable|0
arrow.clockwise.square.fill|SFArrowClockwiseSquareFill|Arrows,Draw,Multicolor|0
arrow.clockwise.square|SFArrowClockwiseSquare|Arrows,Draw|0
arrow.clockwise|SFArrowClockwise|Arrows,Draw|0
arrow.counterclockwise.circle.fill|SFArrowCounterclockwiseCircleFill|Arrows,Draw,Multicolor|0
arrow.counterclockwise.circle|SFArrowCounterclockwiseCircle|Arrows,Draw,Variable|0
arrow.counterclockwise.square.fill|SFArrowCounterclockwiseSquareFill|Arrows,Draw,Multicolor|0
arrow.counterclockwise.square|SFArrowCounterclockwiseSquare|Arrows,Draw|0
arrow.counterclockwise|SFArrowCounterclockwise|Arrows,Draw|0
arrow.down.and.line.horizontal.and.arrow.up|SFArrowDownAndLineHorizontalAndArrowUp|Arrows,Draw|0
arrow.down.app.dashed|SFArrowDownAppDashed|Draw|0
arrow.down.app.dashed.trianglebadge.exclamationmark|SFArrowDownAppDashedTrianglebadgeExclamationmark|Multicolor|0
arrow.down.app.fill|SFArrowDownAppFill|Arrows,Draw,Multicolor|0
arrow.down.app|SFArrowDownApp|Arrows,Draw|0
arrow.down.applewatch|SFArrowDownApplewatch|Devices|1
arrow.down.backward.and.arrow.up.forward.circle.fill|SFArrowDownBackwardAndArrowUpForwardCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.backward.and.arrow.up.forward.circle|SFArrowDownBackwardAndArrowUpForwardCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.down.backward.and.arrow.up.forward.rectangle.fill|SFArrowDownBackwardAndArrowUpForwardRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.backward.and.arrow.up.forward.rectangle|SFArrowDownBackwardAndArrowUpForwardRectangle|Arrows,Draw|0
arrow.down.backward.and.arrow.up.forward.square.fill|SFArrowDownBackwardAndArrowUpForwardSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.backward.and.arrow.up.forward.square|SFArrowDownBackwardAndArrowUpForwardSquare|Arrows,Camera & Photos,Draw|0
arrow.down.backward.and.arrow.up.forward|SFArrowDownBackwardAndArrowUpForward|Arrows,Camera & Photos|0
arrow.down.backward.circle.dotted|SFArrowDownBackwardCircleDotted|Arrows,Draw|0
arrow.down.backward.circle.fill|SFArrowDownBackwardCircleFill|Arrows,Draw,Multicolor|0
arrow.down.backward.circle|SFArrowDownBackwardCircle|Arrows,Draw,Variable|0
arrow.down.backward.square.fill|SFArrowDownBackwardSquareFill|Arrows,Draw,Multicolor|0
arrow.down.backward.square|SFArrowDownBackwardSquare|Arrows,Draw|0
arrow.down.backward|SFArrowDownBackward|Arrows,Draw|0
arrow.down.backward.toptrailing.rectangle.fill|SFArrowDownBackwardToptrailingRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.backward.toptrailing.rectangle|SFArrowDownBackwardToptrailingRectangle|Arrows,Draw|0
arrow.down.circle.badge.pause.fill|SFArrowDownCircleBadgePauseFill|Arrows,Multicolor|0
arrow.down.circle.badge.pause|SFArrowDownCircleBadgePause|Arrows,Multicolor,Variable|0
arrow.down.circle.badge.xmark.fill|SFArrowDownCircleBadgeXmarkFill|Arrows,Multicolor|0
arrow.down.circle.badge.xmark|SFArrowDownCircleBadgeXmark|Arrows,Multicolor,Variable|0
arrow.down.circle.dotted|SFArrowDownCircleDotted|Arrows,Draw|0
arrow.down.circle.fill|SFArrowDownCircleFill|Arrows,Draw,Multicolor|0
arrow.down.circle|SFArrowDownCircle|Arrows,Draw,Variable|0
arrow.down.document.fill|SFArrowDownDocumentFill|Draw,Multicolor,Objects & Tools|0
arrow.down.document|SFArrowDownDocument|Draw,Objects & Tools|0
arrow.down.forward.and.arrow.up.backward.circle.fill|SFArrowDownForwardAndArrowUpBackwardCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.forward.and.arrow.up.backward.circle|SFArrowDownForwardAndArrowUpBackwardCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.down.forward.and.arrow.up.backward.rectangle.fill|SFArrowDownForwardAndArrowUpBackwardRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.forward.and.arrow.up.backward.rectangle|SFArrowDownForwardAndArrowUpBackwardRectangle|Arrows,Draw|0
arrow.down.forward.and.arrow.up.backward.square.fill|SFArrowDownForwardAndArrowUpBackwardSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.forward.and.arrow.up.backward.square|SFArrowDownForwardAndArrowUpBackwardSquare|Arrows,Camera & Photos,Draw|0
arrow.down.forward.and.arrow.up.backward|SFArrowDownForwardAndArrowUpBackward|Arrows,Camera & Photos|0
arrow.down.forward.circle.dotted|SFArrowDownForwardCircleDotted|Arrows,Draw|0
arrow.down.forward.circle.fill|SFArrowDownForwardCircleFill|Arrows,Draw,Multicolor|0
arrow.down.forward.circle|SFArrowDownForwardCircle|Arrows,Draw,Variable|0
arrow.down.forward.square.fill|SFArrowDownForwardSquareFill|Arrows,Draw,Multicolor|0
arrow.down.forward.square|SFArrowDownForwardSquare|Arrows,Draw|0
arrow.down.forward|SFArrowDownForward|Arrows,Draw|0
arrow.down.forward.topleading.rectangle.fill|SFArrowDownForwardTopleadingRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.forward.topleading.rectangle|SFArrowDownForwardTopleadingRectangle|Arrows,Draw|0
arrow.down.heart.fill|SFArrowDownHeartFill|Arrows,Draw,Multicolor|0
arrow.down.heart|SFArrowDownHeart|Arrows,Draw|0
arrow.down.left.and.arrow.up.right.circle.fill|SFArrowDownLeftAndArrowUpRightCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.left.and.arrow.up.right.circle|SFArrowDownLeftAndArrowUpRightCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.down.left.and.arrow.up.right.rectangle.fill|SFArrowDownLeftAndArrowUpRightRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.left.and.arrow.up.right.rectangle|SFArrowDownLeftAndArrowUpRightRectangle|Arrows,Draw|0
arrow.down.left.and.arrow.up.right.square.fill|SFArrowDownLeftAndArrowUpRightSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.left.and.arrow.up.right.square|SFArrowDownLeftAndArrowUpRightSquare|Arrows,Camera & Photos,Draw|0
arrow.down.left.and.arrow.up.right|SFArrowDownLeftAndArrowUpRight|Arrows,Camera & Photos|0
arrow.down.left.arrow.up.right.circle.fill|SFArrowDownLeftArrowUpRightCircleFill|Arrows,Draw,Multicolor|0
arrow.down.left.arrow.up.right.circle|SFArrowDownLeftArrowUpRightCircle|Arrows,Draw,Variable|0
arrow.down.left.arrow.up.right.square.fill|SFArrowDownLeftArrowUpRightSquareFill|Arrows,Draw,Multicolor|0
arrow.down.left.arrow.up.right.square|SFArrowDownLeftArrowUpRightSquare|Arrows,Draw|0
arrow.down.left.arrow.up.right|SFArrowDownLeftArrowUpRight|Arrows,Draw|0
arrow.down.left.circle.dotted|SFArrowDownLeftCircleDotted|Arrows,Draw|0
arrow.down.left.circle.fill|SFArrowDownLeftCircleFill|Arrows,Draw,Multicolor|0
arrow.down.left.circle|SFArrowDownLeftCircle|Arrows,Draw,Variable|0
arrow.down.left.square.fill|SFArrowDownLeftSquareFill|Arrows,Draw,Multicolor|0
arrow.down.left.square|SFArrowDownLeftSquare|Arrows,Draw|0
arrow.down.left|SFArrowDownLeft|Arrows,Draw|0
arrow.down.left.topright.rectangle.fill|SFArrowDownLeftToprightRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.left.topright.rectangle|SFArrowDownLeftToprightRectangle|Arrows,Draw|0
arrow.down.left.video.fill|SFArrowDownLeftVideoFill|Communication,Draw,Multicolor|1
arrow.down.left.video|SFArrowDownLeftVideo|Communication,Draw|1
arrow.down.message.fill|SFArrowDownMessageFill|Communication,Draw,Multicolor|1
arrow.down.message|SFArrowDownMessage|Communication,Draw|1
arrow.down.right.and.arrow.up.left.circle.fill|SFArrowDownRightAndArrowUpLeftCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.right.and.arrow.up.left.circle|SFArrowDownRightAndArrowUpLeftCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.down.right.and.arrow.up.left.rectangle.fill|SFArrowDownRightAndArrowUpLeftRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.right.and.arrow.up.left.rectangle|SFArrowDownRightAndArrowUpLeftRectangle|Arrows,Draw|0
arrow.down.right.and.arrow.up.left.square.fill|SFArrowDownRightAndArrowUpLeftSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.down.right.and.arrow.up.left.square|SFArrowDownRightAndArrowUpLeftSquare|Arrows,Camera & Photos,Draw|0
arrow.down.right.and.arrow.up.left|SFArrowDownRightAndArrowUpLeft|Arrows,Camera & Photos|0
arrow.down.right.circle.dotted|SFArrowDownRightCircleDotted|Arrows,Draw|0
arrow.down.right.circle.fill|SFArrowDownRightCircleFill|Arrows,Draw,Multicolor|0
arrow.down.right.circle|SFArrowDownRightCircle|Arrows,Draw,Variable|0
arrow.down.right.square.fill|SFArrowDownRightSquareFill|Arrows,Draw,Multicolor|0
arrow.down.right.square|SFArrowDownRightSquare|Arrows,Draw|0
arrow.down.right|SFArrowDownRight|Arrows,Draw|0
arrow.down.right.topleft.rectangle.fill|SFArrowDownRightTopleftRectangleFill|Arrows,Draw,Multicolor|0
arrow.down.right.topleft.rectangle|SFArrowDownRightTopleftRectangle|Arrows,Draw|0
arrow.down.square.fill|SFArrowDownSquareFill|Arrows,Draw,Multicolor|0
arrow.down.square|SFArrowDownSquare|Arrows,Draw|0
arrow.down|SFArrowDown|Arrows,Draw|0
arrow.down.to.line.circle.fill|SFArrowDownToLineCircleFill|Arrows,Multicolor|0
arrow.down.to.line.circle|SFArrowDownToLineCircle|Arrows,Draw,Variable|0
arrow.down.to.line.compact|SFArrowDownToLineCompact|Arrows,Draw,Keyboard|0
arrow.down.to.line.square.fill|SFArrowDownToLineSquareFill|Arrows,Multicolor|0
arrow.down.to.line.square|SFArrowDownToLineSquare|Arrows,Draw|0
arrow.down.to.line|SFArrowDownToLine|Arrows,Draw,Keyboard|0
arrow.forward.circle.dotted|SFArrowForwardCircleDotted|Arrows,Draw|0
arrow.forward.circle.fill|SFArrowForwardCircleFill|Arrows,Draw,Multicolor|0
arrow.forward.circle|SFArrowForwardCircle|Arrows,Draw,Variable|0
arrow.forward.folder.fill|SFArrowForwardFolderFill|Draw,Multicolor,Objects & Tools|0
arrow.forward.folder|SFArrowForwardFolder|Draw,Objects & Tools|0
arrow.forward.square.fill|SFArrowForwardSquareFill|Arrows,Draw,Multicolor|0
arrow.forward.square|SFArrowForwardSquare|Arrows,Draw|0
arrow.forward|SFArrowForward|Arrows,Draw|0
arrow.forward.to.line.circle.fill|SFArrowForwardToLineCircleFill|Arrows,Multicolor|0
arrow.forward.to.line.circle|SFArrowForwardToLineCircle|Arrows,Draw,Variable|0
arrow.forward.to.line.compact|SFArrowForwardToLineCompact|Arrows,Draw,Keyboard|0
arrow.forward.to.line.square.fill|SFArrowForwardToLineSquareFill|Arrows,Multicolor|0
arrow.forward.to.line.square|SFArrowForwardToLineSquare|Arrows,Draw|0
arrow.forward.to.line|SFArrowForwardToLine|Arrows,Draw,Keyboard|0
arrow.left.and.line.vertical.and.arrow.right|SFArrowLeftAndLineVerticalAndArrowRight|Arrows,Draw|0
arrow.left.and.right.circle.fill|SFArrowLeftAndRightCircleFill|Arrows,Draw,Multicolor|0
arrow.left.and.right.circle|SFArrowLeftAndRightCircle|Arrows,Draw,Variable|0
arrow.left.and.right.square.fill|SFArrowLeftAndRightSquareFill|Arrows,Draw,Multicolor|0
arrow.left.and.right.square|SFArrowLeftAndRightSquare|Arrows,Draw|0
arrow.left.and.right|SFArrowLeftAndRight|Arrows,Draw|0
arrow.left.and.right.text.vertical|SFArrowLeftAndRightTextVertical|Draw,Text Formatting|0
arrow.left.arrow.right.circle.fill|SFArrowLeftArrowRightCircleFill|Arrows,Draw,Multicolor|0
arrow.left.arrow.right.circle|SFArrowLeftArrowRightCircle|Arrows,Draw,Variable|0
arrow.left.arrow.right.square.fill|SFArrowLeftArrowRightSquareFill|Arrows,Draw,Multicolor|0
arrow.left.arrow.right.square|SFArrowLeftArrowRightSquare|Arrows,Draw|0
arrow.left.arrow.right|SFArrowLeftArrowRight|Arrows,Draw|0
arrow.left.circle.dotted|SFArrowLeftCircleDotted|Arrows,Draw|0
arrow.left.circle.fill|SFArrowLeftCircleFill|Arrows,Draw,Multicolor|0
arrow.left.circle|SFArrowLeftCircle|Arrows,Draw,Variable|0
arrow.left.square.fill|SFArrowLeftSquareFill|Arrows,Draw,Multicolor|0
arrow.left.square|SFArrowLeftSquare|Arrows,Draw|0
arrow.left|SFArrowLeft|Arrows,Draw|0
arrow.left.to.line.circle.fill|SFArrowLeftToLineCircleFill|Arrows,Multicolor|0
arrow.left.to.line.circle|SFArrowLeftToLineCircle|Arrows,Draw,Variable|0
arrow.left.to.line.compact|SFArrowLeftToLineCompact|Arrows,Draw,Keyboard|0
arrow.left.to.line.square.fill|SFArrowLeftToLineSquareFill|Arrows,Multicolor|0
arrow.left.to.line.square|SFArrowLeftToLineSquare|Arrows,Draw|0
arrow.left.to.line|SFArrowLeftToLine|Arrows,Draw,Keyboard|0
arrow.right.and.line.vertical.and.arrow.left|SFArrowRightAndLineVerticalAndArrowLeft|Arrows,Draw|0
arrow.right.circle.dotted|SFArrowRightCircleDotted|Arrows,Draw|0
arrow.right.circle.fill|SFArrowRightCircleFill|Arrows,Draw,Multicolor|0
arrow.right.circle|SFArrowRightCircle|Arrows,Draw,Variable|0
arrow.right.filled.filter.arrow.right|SFArrowRightFilledFilterArrowRight|Automotive|0
arrow.right.page.on.clipboard|SFArrowRightPageOnClipboard|Draw,Objects & Tools|0
arrow.right.square.fill|SFArrowRightSquareFill|Arrows,Draw,Multicolor|0
arrow.right.square|SFArrowRightSquare|Arrows,Draw|0
arrow.right|SFArrowRight|Arrows,Draw|0
arrow.right.to.line.circle.fill|SFArrowRightToLineCircleFill|Arrows,Multicolor|0
arrow.right.to.line.circle|SFArrowRightToLineCircle|Arrows,Draw,Variable|0
arrow.right.to.line.compact|SFArrowRightToLineCompact|Arrows,Draw,Keyboard|0
arrow.right.to.line.square.fill|SFArrowRightToLineSquareFill|Arrows,Multicolor|0
arrow.right.to.line.square|SFArrowRightToLineSquare|Arrows,Draw|0
arrow.right.to.line|SFArrowRightToLine|Arrows,Draw,Keyboard|0
arrow.trianglehead.2.clockwise.rotate.90.camera.fill|SFArrowTrianglehead2ClockwiseRotate90CameraFill|Camera & Photos,Draw,Multicolor,Objects & Tools|0
arrow.trianglehead.2.clockwise.rotate.90.camera|SFArrowTrianglehead2ClockwiseRotate90Camera|Camera & Photos,Draw,Objects & Tools|0
arrow.trianglehead.2.clockwise.rotate.90.circle.fill|SFArrowTrianglehead2ClockwiseRotate90CircleFill|Arrows,Camera & Photos,Draw,Multicolor|0
arrow.trianglehead.2.clockwise.rotate.90.circle|SFArrowTrianglehead2ClockwiseRotate90Circle|Arrows,Camera & Photos,Draw|0
arrow.trianglehead.2.clockwise.rotate.90.icloud.fill|SFArrowTrianglehead2ClockwiseRotate90IcloudFill|Connectivity,Draw,Multicolor|1
arrow.trianglehead.2.clockwise.rotate.90.icloud|SFArrowTrianglehead2ClockwiseRotate90Icloud|Connectivity,Draw|1
arrow.trianglehead.2.clockwise.rotate.90.page.on.clipboard|SFArrowTrianglehead2ClockwiseRotate90PageOnClipboard|Draw,Objects & Tools|0
arrow.trianglehead.2.clockwise.rotate.90|SFArrowTrianglehead2ClockwiseRotate90|Arrows,Camera & Photos,Draw|0
arrow.trianglehead.2.clockwise|SFArrowTrianglehead2Clockwise|Arrows,Draw|0
arrow.trianglehead.2.counterclockwise.rotate.90|SFArrowTrianglehead2CounterclockwiseRotate90|Arrows,Draw|0
arrow.trianglehead.2.counterclockwise|SFArrowTrianglehead2Counterclockwise|Arrows,Draw|0
arrow.trianglehead.bottomleft.capsulepath.clockwise|SFArrowTriangleheadBottomleftCapsulepathClockwise|Arrows,Draw|0
arrow.trianglehead.branch|SFArrowTriangleheadBranch|Arrows,Draw,Maps|0
arrow.trianglehead.clockwise.heart.fill|SFArrowTriangleheadClockwiseHeartFill|Arrows,Draw,Multicolor|0
arrow.trianglehead.clockwise.heart|SFArrowTriangleheadClockwiseHeart|Arrows,Draw|0
arrow.trianglehead.clockwise.icloud.fill|SFArrowTriangleheadClockwiseIcloudFill|Connectivity,Draw,Multicolor|1
arrow.trianglehead.clockwise.icloud|SFArrowTriangleheadClockwiseIcloud|Connectivity,Draw|1
arrow.trianglehead.clockwise.rotate.90|SFArrowTriangleheadClockwiseRotate90|Arrows,Draw,Time|0
arrow.trianglehead.clockwise|SFArrowTriangleheadClockwise|Arrows,Draw,Media|0
arrow.trianglehead.counterclockwise.icloud.fill|SFArrowTriangleheadCounterclockwiseIcloudFill|Connectivity,Draw,Multicolor|1
arrow.trianglehead.counterclockwise.icloud|SFArrowTriangleheadCounterclockwiseIcloud|Connectivity,Draw|1
arrow.trianglehead.counterclockwise.rotate.90|SFArrowTriangleheadCounterclockwiseRotate90|Arrows,Draw,Time|0
arrow.trianglehead.counterclockwise|SFArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
arrow.trianglehead.left.and.right.righttriangle.left.righttriangle.right.fill|SFArrowTriangleheadLeftAndRightRighttriangleLeftRighttriangleRightFill|Camera & Photos,Draw,Editing|0
arrow.trianglehead.left.and.right.righttriangle.left.righttriangle.right|SFArrowTriangleheadLeftAndRightRighttriangleLeftRighttriangleRight|Camera & Photos,Draw,Editing|0
arrow.trianglehead.merge|SFArrowTriangleheadMerge|Arrows,Draw,Maps|0
arrow.trianglehead.pull|SFArrowTriangleheadPull|Arrows,Draw,Maps|0
arrow.trianglehead.rectanglepath|SFArrowTriangleheadRectanglepath|Draw,Media|0
arrow.trianglehead.swap|SFArrowTriangleheadSwap|Arrows,Draw,Maps|0
arrow.trianglehead.topright.capsulepath.clockwise|SFArrowTriangleheadToprightCapsulepathClockwise|Arrows,Draw|0
arrow.trianglehead.turn.up.right.circle.fill|SFArrowTriangleheadTurnUpRightCircleFill|Arrows,Draw,Maps,Multicolor|0
arrow.trianglehead.turn.up.right.circle|SFArrowTriangleheadTurnUpRightCircle|Arrows,Draw,Maps,Multicolor,Variable|0
arrow.trianglehead.turn.up.right.diamond.fill|SFArrowTriangleheadTurnUpRightDiamondFill|Arrows,Draw,Maps,Multicolor|0
arrow.trianglehead.turn.up.right.diamond|SFArrowTriangleheadTurnUpRightDiamond|Arrows,Draw,Maps|0
arrow.trianglehead.turn.up.right|SFArrowTriangleheadTurnUpRight|Arrows,Draw|0
arrow.trianglehead.up.and.down.righttriangle.up.righttriangle.down.fill|SFArrowTriangleheadUpAndDownRighttriangleUpRighttriangleDownFill|Camera & Photos,Draw,Editing|0
arrow.trianglehead.up.and.down.righttriangle.up.righttriangle.down|SFArrowTriangleheadUpAndDownRighttriangleUpRighttriangleDown|Camera & Photos,Draw,Editing|0
arrow.turn.down.left|SFArrowTurnDownLeft|Arrows,Draw,Maps|0
arrow.turn.down.right|SFArrowTurnDownRight|Arrows,Draw,Maps|0
arrow.turn.left.down|SFArrowTurnLeftDown|Arrows,Draw,Maps|0
arrow.turn.left.up|SFArrowTurnLeftUp|Arrows,Draw,Maps|0
arrow.turn.right.down|SFArrowTurnRightDown|Arrows,Draw,Maps|0
arrow.turn.right.up|SFArrowTurnRightUp|Arrows,Draw,Maps|0
arrow.turn.up.forward.iphone.fill|SFArrowTurnUpForwardIphoneFill|Devices,Draw|0
arrow.turn.up.forward.iphone|SFArrowTurnUpForwardIphone|Devices,Draw|0
arrow.turn.up.left|SFArrowTurnUpLeft|Arrows,Draw,Maps|0
arrow.turn.up.right|SFArrowTurnUpRight|Arrows,Draw,Maps|0
arrow.up.and.down.and.arrow.left.and.right|SFArrowUpAndDownAndArrowLeftAndRight|Accessibility,Arrows,Draw,Maps|0
arrow.up.and.down.and.sparkles|SFArrowUpAndDownAndSparkles|Accessibility,Devices,Draw|0
arrow.up.and.down.circle.fill|SFArrowUpAndDownCircleFill|Arrows,Draw,Multicolor|0
arrow.up.and.down.circle|SFArrowUpAndDownCircle|Arrows,Draw,Variable|0
arrow.up.and.down.square.fill|SFArrowUpAndDownSquareFill|Arrows,Draw,Multicolor|0
arrow.up.and.down.square|SFArrowUpAndDownSquare|Arrows,Draw|0
arrow.up.and.down|SFArrowUpAndDown|Arrows,Draw|0
arrow.up.and.down.text.horizontal|SFArrowUpAndDownTextHorizontal|Draw,Text Formatting|0
arrow.up.and.line.horizontal.and.arrow.down|SFArrowUpAndLineHorizontalAndArrowDown|Arrows,Draw|0
arrow.up.and.person.rectangle.portrait|SFArrowUpAndPersonRectanglePortrait|Human|0
arrow.up.and.person.rectangle.turn.left|SFArrowUpAndPersonRectangleTurnLeft|Human|0
arrow.up.and.person.rectangle.turn.right|SFArrowUpAndPersonRectangleTurnRight|Human|0
arrow.up.arrow.down.circle.fill|SFArrowUpArrowDownCircleFill|Arrows,Draw,Multicolor|0
arrow.up.arrow.down.circle|SFArrowUpArrowDownCircle|Arrows,Draw,Variable|0
arrow.up.arrow.down.square.fill|SFArrowUpArrowDownSquareFill|Arrows,Draw,Multicolor|0
arrow.up.arrow.down.square|SFArrowUpArrowDownSquare|Arrows,Draw|0
arrow.up.arrow.down|SFArrowUpArrowDown|Arrows,Draw|0
arrow.up.backward.and.arrow.down.forward.circle.fill|SFArrowUpBackwardAndArrowDownForwardCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.backward.and.arrow.down.forward.circle|SFArrowUpBackwardAndArrowDownForwardCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.up.backward.and.arrow.down.forward.rectangle.fill|SFArrowUpBackwardAndArrowDownForwardRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.backward.and.arrow.down.forward.rectangle|SFArrowUpBackwardAndArrowDownForwardRectangle|Arrows,Draw|0
arrow.up.backward.and.arrow.down.forward.square.fill|SFArrowUpBackwardAndArrowDownForwardSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.backward.and.arrow.down.forward.square|SFArrowUpBackwardAndArrowDownForwardSquare|Arrows,Camera & Photos,Draw|0
arrow.up.backward.and.arrow.down.forward|SFArrowUpBackwardAndArrowDownForward|Arrows,Camera & Photos|0
arrow.up.backward.bottomtrailing.rectangle.fill|SFArrowUpBackwardBottomtrailingRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.backward.bottomtrailing.rectangle|SFArrowUpBackwardBottomtrailingRectangle|Arrows,Draw|0
arrow.up.backward.circle.dotted|SFArrowUpBackwardCircleDotted|Arrows,Draw|0
arrow.up.backward.circle.fill|SFArrowUpBackwardCircleFill|Arrows,Draw,Multicolor|0
arrow.up.backward.circle|SFArrowUpBackwardCircle|Arrows,Draw,Variable|0
arrow.up.backward.square.fill|SFArrowUpBackwardSquareFill|Arrows,Draw,Multicolor|0
arrow.up.backward.square|SFArrowUpBackwardSquare|Arrows,Draw|0
arrow.up.backward|SFArrowUpBackward|Arrows,Draw|0
arrow.up.bin.fill|SFArrowUpBinFill|Draw,Multicolor,Objects & Tools|0
arrow.up.bin|SFArrowUpBin|Draw,Objects & Tools|0
arrow.up.circle.badge.clock|SFArrowUpCircleBadgeClock|Arrows,Multicolor,Variable|0
arrow.up.circle.dotted|SFArrowUpCircleDotted|Arrows,Draw|0
arrow.up.circle.fill|SFArrowUpCircleFill|Arrows,Draw,Multicolor|0
arrow.up.circle|SFArrowUpCircle|Arrows,Draw,Variable|0
arrow.up.document.fill|SFArrowUpDocumentFill|Draw,Multicolor,Objects & Tools|0
arrow.up.document|SFArrowUpDocument|Draw,Objects & Tools|0
arrow.up.folder.fill|SFArrowUpFolderFill|Multicolor,Objects & Tools|0
arrow.up.folder|SFArrowUpFolder|Draw,Objects & Tools|0
arrow.up.forward.and.arrow.down.backward.circle.fill|SFArrowUpForwardAndArrowDownBackwardCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.forward.and.arrow.down.backward.circle|SFArrowUpForwardAndArrowDownBackwardCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.up.forward.and.arrow.down.backward.rectangle.fill|SFArrowUpForwardAndArrowDownBackwardRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.forward.and.arrow.down.backward.rectangle|SFArrowUpForwardAndArrowDownBackwardRectangle|Arrows,Draw|0
arrow.up.forward.and.arrow.down.backward.square.fill|SFArrowUpForwardAndArrowDownBackwardSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.forward.and.arrow.down.backward.square|SFArrowUpForwardAndArrowDownBackwardSquare|Arrows,Camera & Photos,Draw|0
arrow.up.forward.and.arrow.down.backward|SFArrowUpForwardAndArrowDownBackward|Arrows,Camera & Photos|0
arrow.up.forward.app.fill|SFArrowUpForwardAppFill|Arrows,Draw,Multicolor|0
arrow.up.forward.app|SFArrowUpForwardApp|Arrows|0
arrow.up.forward.bottomleading.rectangle.fill|SFArrowUpForwardBottomleadingRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.forward.bottomleading.rectangle|SFArrowUpForwardBottomleadingRectangle|Arrows,Draw|0
arrow.up.forward.circle.dotted|SFArrowUpForwardCircleDotted|Arrows,Draw|0
arrow.up.forward.circle.fill|SFArrowUpForwardCircleFill|Arrows,Draw,Multicolor|0
arrow.up.forward.circle|SFArrowUpForwardCircle|Arrows,Draw,Variable|0
arrow.up.forward.square.fill|SFArrowUpForwardSquareFill|Arrows,Draw,Multicolor|0
arrow.up.forward.square|SFArrowUpForwardSquare|Arrows,Draw|0
arrow.up.forward|SFArrowUpForward|Arrows,Draw|0
arrow.up.heart.fill|SFArrowUpHeartFill|Arrows,Draw,Multicolor|0
arrow.up.heart|SFArrowUpHeart|Arrows,Draw|0
arrow.up.left.and.arrow.down.right.circle.fill|SFArrowUpLeftAndArrowDownRightCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.left.and.arrow.down.right.circle|SFArrowUpLeftAndArrowDownRightCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.up.left.and.arrow.down.right.rectangle.fill|SFArrowUpLeftAndArrowDownRightRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.left.and.arrow.down.right.rectangle|SFArrowUpLeftAndArrowDownRightRectangle|Arrows,Draw|0
arrow.up.left.and.arrow.down.right.square.fill|SFArrowUpLeftAndArrowDownRightSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.left.and.arrow.down.right.square|SFArrowUpLeftAndArrowDownRightSquare|Arrows,Camera & Photos,Draw|0
arrow.up.left.and.arrow.down.right|SFArrowUpLeftAndArrowDownRight|Arrows,Camera & Photos|0
arrow.up.left.and.down.right.and.arrow.up.right.and.down.left|SFArrowUpLeftAndDownRightAndArrowUpRightAndDownLeft|Accessibility,Arrows,Draw,Maps|0
arrow.up.left.and.down.right.magnifyingglass|SFArrowUpLeftAndDownRightMagnifyingglass|Objects & Tools|0
arrow.up.left.arrow.down.right.circle.fill|SFArrowUpLeftArrowDownRightCircleFill|Arrows,Draw,Multicolor|0
arrow.up.left.arrow.down.right.circle|SFArrowUpLeftArrowDownRightCircle|Arrows,Draw,Variable|0
arrow.up.left.arrow.down.right.square.fill|SFArrowUpLeftArrowDownRightSquareFill|Arrows,Multicolor|0
arrow.up.left.arrow.down.right.square|SFArrowUpLeftArrowDownRightSquare|Arrows,Draw|0
arrow.up.left.arrow.down.right|SFArrowUpLeftArrowDownRight|Arrows,Draw|0
arrow.up.left.bottomright.rectangle.fill|SFArrowUpLeftBottomrightRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.left.bottomright.rectangle|SFArrowUpLeftBottomrightRectangle|Arrows,Draw|0
arrow.up.left.circle.dotted|SFArrowUpLeftCircleDotted|Arrows,Draw|0
arrow.up.left.circle.fill|SFArrowUpLeftCircleFill|Arrows,Draw,Multicolor|0
arrow.up.left.circle|SFArrowUpLeftCircle|Arrows,Draw,Variable|0
arrow.up.left.square.fill|SFArrowUpLeftSquareFill|Arrows,Draw,Multicolor|0
arrow.up.left.square|SFArrowUpLeftSquare|Arrows,Draw|0
arrow.up.left|SFArrowUpLeft|Arrows,Draw|0
arrow.up.message.fill|SFArrowUpMessageFill|Communication,Draw,Multicolor|1
arrow.up.message|SFArrowUpMessage|Communication,Draw|1
arrow.up.page.on.clipboard|SFArrowUpPageOnClipboard|Draw,Objects & Tools|0
arrow.up.right.and.arrow.down.left.circle.fill|SFArrowUpRightAndArrowDownLeftCircleFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.right.and.arrow.down.left.circle|SFArrowUpRightAndArrowDownLeftCircle|Arrows,Camera & Photos,Draw,Variable|0
arrow.up.right.and.arrow.down.left.rectangle.fill|SFArrowUpRightAndArrowDownLeftRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.right.and.arrow.down.left.rectangle|SFArrowUpRightAndArrowDownLeftRectangle|Arrows,Draw|0
arrow.up.right.and.arrow.down.left.square.fill|SFArrowUpRightAndArrowDownLeftSquareFill|Arrows,Camera & Photos,Multicolor|0
arrow.up.right.and.arrow.down.left.square|SFArrowUpRightAndArrowDownLeftSquare|Arrows,Camera & Photos,Draw|0
arrow.up.right.and.arrow.down.left|SFArrowUpRightAndArrowDownLeft|Arrows,Camera & Photos|0
arrow.up.right.bottomleft.rectangle.fill|SFArrowUpRightBottomleftRectangleFill|Arrows,Draw,Multicolor|0
arrow.up.right.bottomleft.rectangle|SFArrowUpRightBottomleftRectangle|Arrows,Draw|0
arrow.up.right.circle.dotted|SFArrowUpRightCircleDotted|Arrows,Draw|0
arrow.up.right.circle.fill|SFArrowUpRightCircleFill|Arrows,Draw,Multicolor|0
arrow.up.right.circle|SFArrowUpRightCircle|Arrows,Draw,Variable|0
arrow.up.right.square.fill|SFArrowUpRightSquareFill|Arrows,Draw,Multicolor|0
arrow.up.right.square|SFArrowUpRightSquare|Arrows,Draw|0
arrow.up.right|SFArrowUpRight|Arrows,Draw|0
arrow.up.right.video.fill|SFArrowUpRightVideoFill|Communication,Draw,Multicolor|1
arrow.up.right.video|SFArrowUpRightVideo|Communication,Draw|1
arrow.up.square.fill|SFArrowUpSquareFill|Arrows,Draw,Multicolor|0
arrow.up.square|SFArrowUpSquare|Arrows,Draw|0
arrow.up|SFArrowUp|Arrows,Draw|0
arrow.up.to.line.circle.fill|SFArrowUpToLineCircleFill|Arrows,Multicolor|0
arrow.up.to.line.circle|SFArrowUpToLineCircle|Arrows,Draw,Variable|0
arrow.up.to.line.compact|SFArrowUpToLineCompact|Arrows,Draw,Keyboard|0
arrow.up.to.line.square.fill|SFArrowUpToLineSquareFill|Arrows,Multicolor|0
arrow.up.to.line.square|SFArrowUpToLineSquare|Arrows,Draw|0
arrow.up.to.line|SFArrowUpToLine|Arrows,Draw,Keyboard|0
arrow.up.trash.fill|SFArrowUpTrashFill|Draw,Multicolor,Objects & Tools|0
arrow.up.trash|SFArrowUpTrash|Draw,Multicolor,Objects & Tools|0
arrow.uturn.backward.circle.badge.ellipsis|SFArrowUturnBackwardCircleBadgeEllipsis|Arrows,Multicolor,Variable|0
arrow.uturn.backward.circle.fill|SFArrowUturnBackwardCircleFill|Arrows,Draw,Multicolor|0
arrow.uturn.backward.circle|SFArrowUturnBackwardCircle|Arrows,Draw,Variable|0
arrow.uturn.backward.square.fill|SFArrowUturnBackwardSquareFill|Arrows,Draw,Multicolor|0
arrow.uturn.backward.square|SFArrowUturnBackwardSquare|Arrows,Draw|0
arrow.uturn.backward|SFArrowUturnBackward|Arrows,Draw|0
arrow.uturn.down.circle.fill|SFArrowUturnDownCircleFill|Arrows,Draw,Multicolor|0
arrow.uturn.down.circle|SFArrowUturnDownCircle|Arrows,Draw,Variable|0
arrow.uturn.down.square.fill|SFArrowUturnDownSquareFill|Arrows,Draw,Multicolor|0
arrow.uturn.down.square|SFArrowUturnDownSquare|Arrows,Draw|0
arrow.uturn.down|SFArrowUturnDown|Arrows,Draw|0
arrow.uturn.forward.circle.fill|SFArrowUturnForwardCircleFill|Arrows,Draw,Multicolor|0
arrow.uturn.forward.circle|SFArrowUturnForwardCircle|Arrows,Draw,Variable|0
arrow.uturn.forward.square.fill|SFArrowUturnForwardSquareFill|Arrows,Draw,Multicolor|0
arrow.uturn.forward.square|SFArrowUturnForwardSquare|Arrows,Draw|0
arrow.uturn.forward|SFArrowUturnForward|Arrows,Draw|0
arrow.uturn.left.circle.badge.ellipsis|SFArrowUturnLeftCircleBadgeEllipsis|Arrows,Multicolor,Variable|0
arrow.uturn.left.circle.fill|SFArrowUturnLeftCircleFill|Arrows,Draw,Multicolor|0
arrow.uturn.left.circle|SFArrowUturnLeftCircle|Arrows,Draw,Variable|0
arrow.uturn.left.square.fill|SFArrowUturnLeftSquareFill|Arrows,Draw,Multicolor|0
arrow.uturn.left.square|SFArrowUturnLeftSquare|Arrows,Draw|0
arrow.uturn.left|SFArrowUturnLeft|Arrows,Draw|0
arrow.uturn.right.circle.fill|SFArrowUturnRightCircleFill|Arrows,Draw,Multicolor|0
arrow.uturn.right.circle|SFArrowUturnRightCircle|Arrows,Draw,Variable|0
arrow.uturn.right.square.fill|SFArrowUturnRightSquareFill|Arrows,Draw,Multicolor|0
arrow.uturn.right.square|SFArrowUturnRightSquare|Arrows,Draw|0
arrow.uturn.right|SFArrowUturnRight|Arrows,Draw|0
arrow.uturn.up.circle.fill|SFArrowUturnUpCircleFill|Arrows,Draw,Multicolor|0
arrow.uturn.up.circle|SFArrowUturnUpCircle|Arrows,Draw,Variable|0
arrow.uturn.up.square.fill|SFArrowUturnUpSquareFill|Arrows,Draw,Multicolor|0
arrow.uturn.up.square|SFArrowUturnUpSquare|Arrows,Draw|0
arrow.uturn.up|SFArrowUturnUp|Arrows,Draw|0
arrowkeys.down.filled|SFArrowkeysDownFilled|Gaming|0
arrowkeys.fill|SFArrowkeysFill|Gaming|0
arrowkeys.left.filled|SFArrowkeysLeftFilled|Gaming|0
arrowkeys.right.filled|SFArrowkeysRightFilled|Gaming|0
arrowkeys|SFArrowkeys|Gaming|0
arrowkeys.up.filled|SFArrowkeysUpFilled|Gaming|0
arrowshape.backward.circle.fill|SFArrowshapeBackwardCircleFill|Arrows,Automotive,Multicolor|0
arrowshape.backward.circle|SFArrowshapeBackwardCircle|Arrows,Automotive,Draw,Variable|0
arrowshape.backward.fill|SFArrowshapeBackwardFill|Arrows,Automotive|0
arrowshape.backward|SFArrowshapeBackward|Arrows,Automotive|0
arrowshape.bounce.forward.fill|SFArrowshapeBounceForwardFill|Arrows|0
arrowshape.bounce.forward|SFArrowshapeBounceForward|Arrows|0
arrowshape.bounce.right.fill|SFArrowshapeBounceRightFill|Arrows|0
arrowshape.bounce.right|SFArrowshapeBounceRight|Arrows|0
arrowshape.down.circle.fill|SFArrowshapeDownCircleFill|Arrows,Multicolor|0
arrowshape.down.circle|SFArrowshapeDownCircle|Arrows,Draw,Variable|0
arrowshape.down.fill|SFArrowshapeDownFill|Arrows|0
arrowshape.down|SFArrowshapeDown|Arrows|0
arrowshape.forward.circle.fill|SFArrowshapeForwardCircleFill|Arrows,Automotive,Multicolor|0
arrowshape.forward.circle|SFArrowshapeForwardCircle|Arrows,Automotive,Draw,Variable|0
arrowshape.forward.fill|SFArrowshapeForwardFill|Arrows,Automotive|0
arrowshape.forward|SFArrowshapeForward|Arrows,Automotive|0
arrowshape.left.arrowshape.right.fill|SFArrowshapeLeftArrowshapeRightFill|Arrows,Automotive|0
arrowshape.left.arrowshape.right|SFArrowshapeLeftArrowshapeRight|Arrows,Automotive|0
arrowshape.left.circle.fill|SFArrowshapeLeftCircleFill|Arrows,Automotive,Multicolor|0
arrowshape.left.circle|SFArrowshapeLeftCircle|Arrows,Automotive,Draw,Variable|0
arrowshape.left.fill|SFArrowshapeLeftFill|Arrows,Automotive|0
arrowshape.left|SFArrowshapeLeft|Arrows,Automotive|0
arrowshape.right.circle.fill|SFArrowshapeRightCircleFill|Arrows,Automotive,Multicolor|0
arrowshape.right.circle|SFArrowshapeRightCircle|Arrows,Automotive,Draw,Variable|0
arrowshape.right.fill|SFArrowshapeRightFill|Arrows,Automotive|0
arrowshape.right|SFArrowshapeRight|Arrows,Automotive|0
arrowshape.turn.up.backward.2.circle.fill|SFArrowshapeTurnUpBackward2CircleFill|Arrows,Multicolor|0
arrowshape.turn.up.backward.2.circle|SFArrowshapeTurnUpBackward2Circle|Arrows,Draw,Variable|0
arrowshape.turn.up.backward.2.fill|SFArrowshapeTurnUpBackward2Fill|Arrows|0
arrowshape.turn.up.backward.2|SFArrowshapeTurnUpBackward2|Arrows|0
arrowshape.turn.up.backward.badge.clock.fill|SFArrowshapeTurnUpBackwardBadgeClockFill|Arrows,Multicolor|0
arrowshape.turn.up.backward.badge.clock|SFArrowshapeTurnUpBackwardBadgeClock|Arrows,Multicolor|0
arrowshape.turn.up.backward.circle.fill|SFArrowshapeTurnUpBackwardCircleFill|Arrows,Multicolor|0
arrowshape.turn.up.backward.circle|SFArrowshapeTurnUpBackwardCircle|Arrows,Draw,Variable|0
arrowshape.turn.up.backward.fill|SFArrowshapeTurnUpBackwardFill|Arrows|0
arrowshape.turn.up.backward|SFArrowshapeTurnUpBackward|Arrows|0
arrowshape.turn.up.forward.circle.fill|SFArrowshapeTurnUpForwardCircleFill|Arrows,Multicolor|0
arrowshape.turn.up.forward.circle|SFArrowshapeTurnUpForwardCircle|Arrows,Draw,Variable|0
arrowshape.turn.up.forward.fill|SFArrowshapeTurnUpForwardFill|Arrows|0
arrowshape.turn.up.forward|SFArrowshapeTurnUpForward|Arrows|0
arrowshape.turn.up.left.2.circle.fill|SFArrowshapeTurnUpLeft2CircleFill|Arrows,Multicolor|0
arrowshape.turn.up.left.2.circle|SFArrowshapeTurnUpLeft2Circle|Arrows,Draw,Variable|0
arrowshape.turn.up.left.2.fill|SFArrowshapeTurnUpLeft2Fill|Arrows|0
arrowshape.turn.up.left.2|SFArrowshapeTurnUpLeft2|Arrows|0
arrowshape.turn.up.left.circle.fill|SFArrowshapeTurnUpLeftCircleFill|Arrows,Multicolor|0
arrowshape.turn.up.left.circle|SFArrowshapeTurnUpLeftCircle|Arrows,Draw,Variable|0
arrowshape.turn.up.left.fill|SFArrowshapeTurnUpLeftFill|Arrows|0
arrowshape.turn.up.left|SFArrowshapeTurnUpLeft|Arrows|0
arrowshape.turn.up.right.circle.fill|SFArrowshapeTurnUpRightCircleFill|Arrows,Multicolor|0
arrowshape.turn.up.right.circle|SFArrowshapeTurnUpRightCircle|Arrows,Draw,Variable|0
arrowshape.turn.up.right.fill|SFArrowshapeTurnUpRightFill|Arrows|0
arrowshape.turn.up.right|SFArrowshapeTurnUpRight|Arrows|0
arrowshape.up.circle.fill|SFArrowshapeUpCircleFill|Arrows,Multicolor|0
arrowshape.up.circle|SFArrowshapeUpCircle|Arrows,Draw,Variable|0
arrowshape.up.fill|SFArrowshapeUpFill|Arrows|0
arrowshape.up|SFArrowshapeUp|Arrows|0
arrowshape.zigzag.forward.fill|SFArrowshapeZigzagForwardFill|Arrows|0
arrowshape.zigzag.forward|SFArrowshapeZigzagForward|Arrows|0
arrowshape.zigzag.right.fill|SFArrowshapeZigzagRightFill|Arrows|0
arrowshape.zigzag.right|SFArrowshapeZigzagRight|Arrows|0
arrowtriangle.backward.circle.fill|SFArrowtriangleBackwardCircleFill|Arrows,Multicolor|0
arrowtriangle.backward.circle|SFArrowtriangleBackwardCircle|Arrows,Draw,Variable|0
arrowtriangle.backward.fill|SFArrowtriangleBackwardFill|Arrows|0
arrowtriangle.backward.inset.filled.trailingthird.rectangle|SFArrowtriangleBackwardInsetFilledTrailingthirdRectangle|Draw,Multicolor|0
arrowtriangle.backward.square.fill|SFArrowtriangleBackwardSquareFill|Arrows,Multicolor|0
arrowtriangle.backward.square|SFArrowtriangleBackwardSquare|Arrows,Draw|0
arrowtriangle.backward|SFArrowtriangleBackward|Arrows|0
arrowtriangle.down.2.fill|SFArrowtriangleDown2Fill|Arrows|0
arrowtriangle.down.2|SFArrowtriangleDown2|Arrows|0
arrowtriangle.down.circle.fill|SFArrowtriangleDownCircleFill|Arrows,Gaming,Multicolor|0
arrowtriangle.down.circle|SFArrowtriangleDownCircle|Arrows,Draw,Gaming,Variable|0
arrowtriangle.down.fill|SFArrowtriangleDownFill|Arrows|0
arrowtriangle.down.square.fill|SFArrowtriangleDownSquareFill|Arrows,Multicolor|0
arrowtriangle.down.square|SFArrowtriangleDownSquare|Arrows,Draw|0
arrowtriangle.down|SFArrowtriangleDown|Arrows|0
arrowtriangle.forward.circle.fill|SFArrowtriangleForwardCircleFill|Arrows,Multicolor|0
arrowtriangle.forward.circle|SFArrowtriangleForwardCircle|Arrows,Draw,Variable|0
arrowtriangle.forward.fill|SFArrowtriangleForwardFill|Arrows|0
arrowtriangle.forward.inset.filled.trailingthird.rectangle|SFArrowtriangleForwardInsetFilledTrailingthirdRectangle|Draw,Multicolor|0
arrowtriangle.forward.square.fill|SFArrowtriangleForwardSquareFill|Arrows,Multicolor|0
arrowtriangle.forward.square|SFArrowtriangleForwardSquare|Arrows,Draw|0
arrowtriangle.forward|SFArrowtriangleForward|Arrows|0
arrowtriangle.left.and.line.vertical.and.arrowtriangle.right.fill|SFArrowtriangleLeftAndLineVerticalAndArrowtriangleRightFill|Arrows,Draw|0
arrowtriangle.left.and.line.vertical.and.arrowtriangle.right|SFArrowtriangleLeftAndLineVerticalAndArrowtriangleRight|Arrows,Draw|0
arrowtriangle.left.circle.fill|SFArrowtriangleLeftCircleFill|Arrows,Gaming,Multicolor|0
arrowtriangle.left.circle|SFArrowtriangleLeftCircle|Arrows,Draw,Gaming,Variable|0
arrowtriangle.left.fill|SFArrowtriangleLeftFill|Arrows|0
arrowtriangle.left.square.fill|SFArrowtriangleLeftSquareFill|Arrows,Multicolor|0
arrowtriangle.left.square|SFArrowtriangleLeftSquare|Arrows,Draw|0
arrowtriangle.left|SFArrowtriangleLeft|Arrows|0
arrowtriangle.right.and.line.vertical.and.arrowtriangle.left.fill|SFArrowtriangleRightAndLineVerticalAndArrowtriangleLeftFill|Arrows,Draw|0
arrowtriangle.right.and.line.vertical.and.arrowtriangle.left|SFArrowtriangleRightAndLineVerticalAndArrowtriangleLeft|Arrows,Draw|0
arrowtriangle.right.circle.fill|SFArrowtriangleRightCircleFill|Arrows,Gaming,Multicolor|0
arrowtriangle.right.circle|SFArrowtriangleRightCircle|Arrows,Draw,Gaming,Variable|0
arrowtriangle.right.fill|SFArrowtriangleRightFill|Arrows|0
arrowtriangle.right.square.fill|SFArrowtriangleRightSquareFill|Arrows,Multicolor|0
arrowtriangle.right.square|SFArrowtriangleRightSquare|Arrows,Draw|0
arrowtriangle.right|SFArrowtriangleRight|Arrows|0
arrowtriangle.up.2.fill|SFArrowtriangleUp2Fill|Arrows|0
arrowtriangle.up.2|SFArrowtriangleUp2|Arrows|0
arrowtriangle.up.arrowtriangle.down.window.left|SFArrowtriangleUpArrowtriangleDownWindowLeft|Automotive|0
arrowtriangle.up.arrowtriangle.down.window.right|SFArrowtriangleUpArrowtriangleDownWindowRight|Automotive|0
arrowtriangle.up.circle.fill|SFArrowtriangleUpCircleFill|Arrows,Gaming,Multicolor|0
arrowtriangle.up.circle|SFArrowtriangleUpCircle|Arrows,Draw,Gaming,Variable|0
arrowtriangle.up.fill|SFArrowtriangleUpFill|Arrows|0
arrowtriangle.up.square.fill|SFArrowtriangleUpSquareFill|Arrows,Multicolor|0
arrowtriangle.up.square|SFArrowtriangleUpSquare|Arrows,Draw|0
arrowtriangle.up|SFArrowtriangleUp|Arrows|0
aspectratio.fill|SFAspectratioFill|Editing|0
aspectratio|SFAspectratio|Editing|0
asterisk.circle.fill|SFAsteriskCircleFill|Multicolor|0
asterisk.circle|SFAsteriskCircle|Draw,Variable|0
asterisk|SFAsterisk||0
at.badge.minus|SFAtBadgeMinus|Multicolor|0
at.badge.plus|SFAtBadgePlus|Multicolor|0
at.circle.fill|SFAtCircleFill|Multicolor|0
at.circle|SFAtCircle|Draw,Multicolor,Variable|0
at|SFAt|Multicolor|0
atom|SFAtom|Draw,Nature|0
audio.jack.mono|SFAudioJackMono|Devices|0
audio.jack.stereo|SFAudioJackStereo|Devices|0
australian.football.circle.fill|SFAustralianFootballCircleFill|Fitness,Multicolor,Objects & Tools|0
australian.football.circle|SFAustralianFootballCircle|Draw,Fitness,Objects & Tools,Variable|0
australian.football.fill|SFAustralianFootballFill|Fitness,Objects & Tools|0
australian.football|SFAustralianFootball|Fitness,Objects & Tools|0
australiandollarsign.arrow.trianglehead.counterclockwise.rotate.90|SFAustraliandollarsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
australiandollarsign.bank.building.fill|SFAustraliandollarsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
australiandollarsign.bank.building|SFAustraliandollarsignBankBuilding|Commerce,Objects & Tools|0
australiandollarsign.circle.fill|SFAustraliandollarsignCircleFill|Commerce,Indices,Multicolor|0
australiandollarsign.circle|SFAustraliandollarsignCircle|Commerce,Draw,Indices,Variable|0
australiandollarsign.gauge.chart.lefthalf.righthalf|SFAustraliandollarsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
australiandollarsign.gauge.chart.leftthird.topthird.rightthird|SFAustraliandollarsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
australiandollarsign.ring.dashed|SFAustraliandollarsignRingDashed|Commerce,Home,Variable|0
australiandollarsign.ring|SFAustraliandollarsignRing|Commerce,Draw,Home|0
australiandollarsign.square.fill|SFAustraliandollarsignSquareFill|Commerce,Indices,Multicolor|0
australiandollarsign.square|SFAustraliandollarsignSquare|Commerce,Draw,Indices|0
australiandollarsign|SFAustraliandollarsign|Commerce,Indices|0
australsign.arrow.trianglehead.counterclockwise.rotate.90|SFAustralsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
australsign.bank.building.fill|SFAustralsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
australsign.bank.building|SFAustralsignBankBuilding|Commerce,Objects & Tools|0
australsign.circle.fill|SFAustralsignCircleFill|Commerce,Indices,Multicolor|0
australsign.circle|SFAustralsignCircle|Commerce,Draw,Indices,Variable|0
australsign.gauge.chart.lefthalf.righthalf|SFAustralsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
australsign.gauge.chart.leftthird.topthird.rightthird|SFAustralsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
australsign.ring.dashed|SFAustralsignRingDashed|Commerce,Home,Variable|0
australsign.ring|SFAustralsignRing|Commerce,Draw,Home|0
australsign.square.fill|SFAustralsignSquareFill|Commerce,Indices,Multicolor|0
australsign.square|SFAustralsignSquare|Commerce,Draw,Indices|0
australsign|SFAustralsign|Commerce,Indices|0
automatic.brakesignal|SFAutomaticBrakesignal|Automotive|0
automatic.headlight.high.beam.fill|SFAutomaticHeadlightHighBeamFill|Automotive,Multicolor|0
automatic.headlight.high.beam|SFAutomaticHeadlightHighBeam|Automotive,Multicolor|0
automatic.headlight.low.beam.fill|SFAutomaticHeadlightLowBeamFill|Automotive,Multicolor|0
automatic.headlight.low.beam|SFAutomaticHeadlightLowBeam|Automotive,Multicolor|0
autostartstop.slash|SFAutostartstopSlash|Automotive,Multicolor|0
autostartstop|SFAutostartstop|Automotive,Multicolor|0
autostartstop.trianglebadge.exclamationmark|SFAutostartstopTrianglebadgeExclamationmark|Automotive,Multicolor|0
av.remote.fill|SFAvRemoteFill|Devices,Home|0
av.remote|SFAvRemote|Devices,Home|0
axle.2.driveshaft.disengaged|SFAxle2DriveshaftDisengaged|Automotive|0
axle.2.front.and.rear.engaged|SFAxle2FrontAndRearEngaged|Automotive|0
axle.2.front.disengaged|SFAxle2FrontDisengaged|Automotive|0
axle.2.front.engaged|SFAxle2FrontEngaged|Automotive|0
axle.2.rear.disengaged|SFAxle2RearDisengaged|Automotive|0
axle.2.rear.engaged|SFAxle2RearEngaged|Automotive|0
axle.2.rear.lock|SFAxle2RearLock|Automotive|0
axle.2|SFAxle2|Automotive|0
b.circle.fill|SFBCircleFill|Gaming,Indices,Multicolor|0
b.circle|SFBCircle|Draw,Gaming,Indices,Variable|0
b.square.fill|SFBSquareFill|Indices,Multicolor|0
b.square|SFBSquare|Draw,Indices|0
backpack.circle.fill|SFBackpackCircleFill|Multicolor,Objects & Tools|0
backpack.circle|SFBackpackCircle|Draw,Objects & Tools,Variable|0
backpack.fill|SFBackpackFill|Objects & Tools|0
backpack.sensor.tag.radiowaves.left.and.right.fill|SFBackpackSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
backpack.sensor.tag.radiowaves.left.and.right|SFBackpackSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
backpack|SFBackpack|Objects & Tools|0
backward.circle.fill|SFBackwardCircleFill|Media,Multicolor|0
backward.circle|SFBackwardCircle|Draw,Media,Variable|0
backward.end.alt.fill|SFBackwardEndAltFill|Media|0
backward.end.alt|SFBackwardEndAlt|Media|0
backward.end.circle.fill|SFBackwardEndCircleFill|Media,Multicolor|0
backward.end.circle|SFBackwardEndCircle|Draw,Media,Variable|0
backward.end.fill|SFBackwardEndFill|Media|0
backward.end|SFBackwardEnd|Media|0
backward.fill|SFBackwardFill|Media|0
backward.frame.fill|SFBackwardFrameFill|Media|0
backward.frame|SFBackwardFrame|Media|0
backward|SFBackward|Media|0
badge.plus.radiowaves.forward|SFBadgePlusRadiowavesForward|Draw,Multicolor,Variable|0
badge.plus.radiowaves.right|SFBadgePlusRadiowavesRight|Draw,Multicolor,Variable|0
bag.badge.minus|SFBagBadgeMinus|Commerce,Multicolor,Objects & Tools|0
bag.badge.plus|SFBagBadgePlus|Commerce,Multicolor,Objects & Tools|0
bag.badge.questionmark|SFBagBadgeQuestionmark|Commerce,Multicolor,Objects & Tools|0
bag.circle.fill|SFBagCircleFill|Commerce,Multicolor,Objects & Tools|0
bag.circle|SFBagCircle|Commerce,Draw,Objects & Tools,Variable|0
bag.fill.badge.minus|SFBagFillBadgeMinus|Commerce,Multicolor,Objects & Tools|0
bag.fill.badge.plus|SFBagFillBadgePlus|Commerce,Multicolor,Objects & Tools|0
bag.fill.badge.questionmark|SFBagFillBadgeQuestionmark|Commerce,Multicolor,Objects & Tools|0
bag.fill|SFBagFill|Commerce,Objects & Tools|0
bag|SFBag|Commerce,Objects & Tools|0
bahtsign.arrow.trianglehead.counterclockwise.rotate.90|SFBahtsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
bahtsign.bank.building.fill|SFBahtsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
bahtsign.bank.building|SFBahtsignBankBuilding|Commerce,Objects & Tools|0
bahtsign.circle.fill|SFBahtsignCircleFill|Commerce,Indices,Multicolor|0
bahtsign.circle|SFBahtsignCircle|Commerce,Draw,Indices,Variable|0
bahtsign.gauge.chart.lefthalf.righthalf|SFBahtsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
bahtsign.gauge.chart.leftthird.topthird.rightthird|SFBahtsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
bahtsign.ring.dashed|SFBahtsignRingDashed|Commerce,Home,Variable|0
bahtsign.ring|SFBahtsignRing|Commerce,Draw,Home|0
bahtsign.square.fill|SFBahtsignSquareFill|Commerce,Indices,Multicolor|0
bahtsign.square|SFBahtsignSquare|Commerce,Draw,Indices|0
bahtsign|SFBahtsign|Commerce,Indices|0
balloon.2.fill|SFBalloon2Fill|Home,Objects & Tools|0
balloon.2|SFBalloon2|Home,Objects & Tools|0
balloon.fill|SFBalloonFill|Home,Objects & Tools|0
balloon|SFBalloon|Home,Objects & Tools|0
bandage.fill|SFBandageFill|Editing,Health,Objects & Tools|0
bandage|SFBandage|Editing,Health,Objects & Tools|0
banknote.fill|SFBanknoteFill|Commerce|0
banknote|SFBanknote|Commerce|0
barcode|SFBarcode||0
barcode.viewfinder|SFBarcodeViewfinder||0
barometer|SFBarometer|Objects & Tools|0
base.unit|SFBaseUnit|Maps|0
baseball.circle.fill|SFBaseballCircleFill|Fitness,Multicolor,Objects & Tools|0
baseball.circle|SFBaseballCircle|Draw,Fitness,Objects & Tools,Variable|0
baseball.diamond.bases.outs.indicator|SFBaseballDiamondBasesOutsIndicator|Fitness|0
baseball.diamond.bases|SFBaseballDiamondBases|Fitness|0
baseball.fill|SFBaseballFill|Fitness,Objects & Tools|0
baseball|SFBaseball|Fitness,Objects & Tools|0
basket.fill|SFBasketFill|Commerce,Objects & Tools|0
basket|SFBasket|Commerce,Objects & Tools|0
basketball.circle.fill|SFBasketballCircleFill|Fitness,Multicolor,Objects & Tools|0
basketball.circle|SFBasketballCircle|Draw,Fitness,Objects & Tools,Variable|0
basketball.fill|SFBasketballFill|Fitness,Objects & Tools|0
basketball|SFBasketball|Fitness,Objects & Tools|0
bathtub.fill|SFBathtubFill|Home,Objects & Tools|0
bathtub|SFBathtub|Home,Objects & Tools|0
battery.0percent|SFBattery0percent|Multicolor,Objects & Tools|0
battery.100percent.bolt|SFBattery100percentBolt|Multicolor,Objects & Tools|0
battery.100percent.circle.fill|SFBattery100percentCircleFill|Multicolor,Objects & Tools|0
battery.100percent.circle|SFBattery100percentCircle|Draw,Multicolor,Objects & Tools,Variable|0
battery.100percent|SFBattery100percent|Multicolor,Objects & Tools|0
battery.25percent|SFBattery25percent|Multicolor,Objects & Tools|0
battery.50percent|SFBattery50percent|Multicolor,Objects & Tools|0
battery.75percent|SFBattery75percent|Multicolor,Objects & Tools|0
batteryblock.fill|SFBatteryblockFill|Automotive,Objects & Tools|0
batteryblock.slash.fill|SFBatteryblockSlashFill|Automotive,Draw,Objects & Tools|0
batteryblock.slash|SFBatteryblockSlash|Automotive,Draw,Objects & Tools|0
batteryblock.stack.badge.snowflake.fill|SFBatteryblockStackBadgeSnowflakeFill|Automotive,Objects & Tools|0
batteryblock.stack.badge.snowflake|SFBatteryblockStackBadgeSnowflake|Automotive,Objects & Tools|0
batteryblock.stack.fill|SFBatteryblockStackFill|Automotive,Objects & Tools|0
batteryblock.stack|SFBatteryblockStack|Automotive,Objects & Tools|0
batteryblock.stack.trianglebadge.exclamationmark.fill|SFBatteryblockStackTrianglebadgeExclamationmarkFill|Automotive,Multicolor,Objects & Tools|0
batteryblock.stack.trianglebadge.exclamationmark|SFBatteryblockStackTrianglebadgeExclamationmark|Automotive,Multicolor,Objects & Tools|0
batteryblock|SFBatteryblock|Automotive,Objects & Tools|0
beach.umbrella.fill|SFBeachUmbrellaFill|Objects & Tools|0
beach.umbrella|SFBeachUmbrella|Objects & Tools|0
beats.earphones|SFBeatsEarphones|Devices|1
beats.fitpro.chargingcase.fill|SFBeatsFitproChargingcaseFill|Devices|1
beats.fitpro.chargingcase|SFBeatsFitproChargingcase|Devices|1
beats.fitpro.left|SFBeatsFitproLeft|Devices|1
beats.fitpro.right|SFBeatsFitproRight|Devices|1
beats.fitpro|SFBeatsFitpro|Devices|1
beats.headphones|SFBeatsHeadphones|Devices,Objects & Tools|1
beats.pill.fill|SFBeatsPillFill|Devices|1
beats.pill|SFBeatsPill|Devices|1
beats.powerbeats.left|SFBeatsPowerbeatsLeft|Devices|1
beats.powerbeats.pro.2.chargingcase.fill|SFBeatsPowerbeatsPro2ChargingcaseFill|Devices|1
beats.powerbeats.pro.2.chargingcase|SFBeatsPowerbeatsPro2Chargingcase|Devices|1
beats.powerbeats.pro.2.left|SFBeatsPowerbeatsPro2Left|Devices|1
beats.powerbeats.pro.2.right|SFBeatsPowerbeatsPro2Right|Devices|1
beats.powerbeats.pro.2|SFBeatsPowerbeatsPro2|Devices|1
beats.powerbeats.pro.chargingcase.fill|SFBeatsPowerbeatsProChargingcaseFill|Devices|1
beats.powerbeats.pro.chargingcase|SFBeatsPowerbeatsProChargingcase|Devices|1
beats.powerbeats.pro.left|SFBeatsPowerbeatsProLeft|Devices|1
beats.powerbeats.pro.right|SFBeatsPowerbeatsProRight|Devices|1
beats.powerbeats.pro|SFBeatsPowerbeatsPro|Devices|1
beats.powerbeats.right|SFBeatsPowerbeatsRight|Devices|1
beats.powerbeats|SFBeatsPowerbeats|Devices|1
beats.powerbeats3.left|SFBeatsPowerbeats3Left|Devices|1
beats.powerbeats3.right|SFBeatsPowerbeats3Right|Devices|1
beats.powerbeats3|SFBeatsPowerbeats3|Devices|1
beats.solobuds.chargingcase.fill|SFBeatsSolobudsChargingcaseFill|Devices|1
beats.solobuds.chargingcase|SFBeatsSolobudsChargingcase|Devices|1
beats.solobuds.left|SFBeatsSolobudsLeft|Devices|1
beats.solobuds.right|SFBeatsSolobudsRight|Devices|1
beats.solobuds|SFBeatsSolobuds|Devices|1
beats.studiobuds.chargingcase.fill|SFBeatsStudiobudsChargingcaseFill|Devices|1
beats.studiobuds.chargingcase|SFBeatsStudiobudsChargingcase|Devices|1
beats.studiobuds.left|SFBeatsStudiobudsLeft|Devices|1
beats.studiobuds.plus.chargingcase.fill|SFBeatsStudiobudsPlusChargingcaseFill|Devices|1
beats.studiobuds.plus.chargingcase|SFBeatsStudiobudsPlusChargingcase|Devices|1
beats.studiobuds.plus.left|SFBeatsStudiobudsPlusLeft|Devices|1
beats.studiobuds.plus.right|SFBeatsStudiobudsPlusRight|Devices|1
beats.studiobuds.plus|SFBeatsStudiobudsPlus|Devices|1
beats.studiobuds.right|SFBeatsStudiobudsRight|Devices|1
beats.studiobuds|SFBeatsStudiobuds|Devices|1
bed.double.badge.checkmark.fill|SFBedDoubleBadgeCheckmarkFill|Health,Home,Multicolor,Objects & Tools|0
bed.double.badge.checkmark|SFBedDoubleBadgeCheckmark|Health,Home,Multicolor,Objects & Tools|0
bed.double.circle.fill|SFBedDoubleCircleFill|Health,Home,Multicolor,Objects & Tools|0
bed.double.circle|SFBedDoubleCircle|Draw,Health,Home,Multicolor,Objects & Tools,Variable|0
bed.double.fill|SFBedDoubleFill|Health,Home,Multicolor,Objects & Tools|0
bed.double|SFBedDouble|Health,Home,Multicolor,Objects & Tools|0
bell.and.waves.left.and.right.fill|SFBellAndWavesLeftAndRightFill|Draw,Objects & Tools,Variable|0
bell.and.waves.left.and.right|SFBellAndWavesLeftAndRight|Draw,Objects & Tools,Variable|0
bell.badge.circle.fill|SFBellBadgeCircleFill|Multicolor,Objects & Tools|0
bell.badge.circle|SFBellBadgeCircle|Draw,Multicolor,Objects & Tools,Variable|0
bell.badge.fill|SFBellBadgeFill|Multicolor,Objects & Tools|0
bell.badge.slash.fill|SFBellBadgeSlashFill|Draw,Objects & Tools|0
bell.badge.slash|SFBellBadgeSlash|Draw,Objects & Tools|0
bell.badge|SFBellBadge|Multicolor,Objects & Tools|0
bell.badge.waveform.fill|SFBellBadgeWaveformFill|Draw,Objects & Tools,Variable|0
bell.badge.waveform.slash.fill|SFBellBadgeWaveformSlashFill|Draw,Objects & Tools|0
bell.badge.waveform.slash|SFBellBadgeWaveformSlash|Draw,Objects & Tools|0
bell.badge.waveform|SFBellBadgeWaveform|Draw,Objects & Tools,Variable|0
bell.circle.fill|SFBellCircleFill|Multicolor,Objects & Tools|0
bell.circle|SFBellCircle|Draw,Multicolor,Objects & Tools,Variable|0
bell.fill|SFBellFill|Multicolor,Objects & Tools|0
bell.slash.circle.fill|SFBellSlashCircleFill|Multicolor,Objects & Tools|0
bell.slash.circle|SFBellSlashCircle|Draw,Multicolor,Objects & Tools,Variable|0
bell.slash.fill|SFBellSlashFill|Draw,Multicolor,Objects & Tools|0
bell.slash|SFBellSlash|Draw,Multicolor,Objects & Tools|0
bell.square.fill|SFBellSquareFill|Multicolor,Objects & Tools|0
bell.square|SFBellSquare|Draw,Multicolor,Objects & Tools|0
bell|SFBell|Multicolor,Objects & Tools|0
beziercurve|SFBeziercurve|Editing|0
bicycle.circle.fill|SFBicycleCircleFill|Maps,Multicolor,Transportation|0
bicycle.circle|SFBicycleCircle|Draw,Maps,Transportation,Variable|0
bicycle.sensor.tag.radiowaves.left.and.right.fill|SFBicycleSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
bicycle.sensor.tag.radiowaves.left.and.right|SFBicycleSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
bicycle|SFBicycle|Maps,Transportation|0
binoculars.circle.fill|SFBinocularsCircleFill|Maps,Multicolor,Objects & Tools|0
binoculars.circle|SFBinocularsCircle|Draw,Maps,Objects & Tools,Variable|0
binoculars.fill|SFBinocularsFill|Maps,Objects & Tools|0
binoculars|SFBinoculars|Maps,Objects & Tools|0
bird.circle.fill|SFBirdCircleFill|Multicolor,Nature|0
bird.circle|SFBirdCircle|Draw,Nature,Variable|0
bird.fill|SFBirdFill|Nature|0
bird|SFBird|Nature|0
birthday.cake.fill|SFBirthdayCakeFill|Objects & Tools|0
birthday.cake|SFBirthdayCake|Objects & Tools|0
bitcoinsign.arrow.trianglehead.counterclockwise.rotate.90|SFBitcoinsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
bitcoinsign.bank.building.fill|SFBitcoinsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
bitcoinsign.bank.building|SFBitcoinsignBankBuilding|Commerce,Objects & Tools|0
bitcoinsign.circle.fill|SFBitcoinsignCircleFill|Commerce,Indices,Multicolor|0
bitcoinsign.circle|SFBitcoinsignCircle|Commerce,Draw,Indices,Variable|0
bitcoinsign.gauge.chart.lefthalf.righthalf|SFBitcoinsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
bitcoinsign.gauge.chart.leftthird.topthird.rightthird|SFBitcoinsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
bitcoinsign.ring.dashed|SFBitcoinsignRingDashed|Commerce,Home,Variable|0
bitcoinsign.ring|SFBitcoinsignRing|Commerce,Draw,Home|0
bitcoinsign.square.fill|SFBitcoinsignSquareFill|Commerce,Indices,Multicolor|0
bitcoinsign.square|SFBitcoinsignSquare|Commerce,Draw,Indices|0
bitcoinsign|SFBitcoinsign|Commerce,Indices|0
blinds.horizontal.closed|SFBlindsHorizontalClosed|Home|0
blinds.horizontal.open|SFBlindsHorizontalOpen|Home|0
blinds.vertical.closed|SFBlindsVerticalClosed|Home|0
blinds.vertical.open|SFBlindsVerticalOpen|Home|0
blood.pressure.cuff.badge.gauge.with.needle.fill|SFBloodPressureCuffBadgeGaugeWithNeedleFill|Health,Multicolor,Objects & Tools|0
blood.pressure.cuff.badge.gauge.with.needle|SFBloodPressureCuffBadgeGaugeWithNeedle|Health,Multicolor,Objects & Tools|0
blood.pressure.cuff.fill|SFBloodPressureCuffFill|Health,Objects & Tools|0
blood.pressure.cuff|SFBloodPressureCuff|Health,Objects & Tools|0
bold.italic.underline|SFBoldItalicUnderline|Draw,Multicolor,Text Formatting|0
bold|SFBold|Multicolor,Text Formatting|0
bold.underline|SFBoldUnderline|Draw,Multicolor,Text Formatting|0
bolt.badge.automatic.fill|SFBoltBadgeAutomaticFill|Camera & Photos,Multicolor,Nature|0
bolt.badge.automatic|SFBoltBadgeAutomatic|Camera & Photos,Multicolor,Nature|0
bolt.badge.checkmark.fill|SFBoltBadgeCheckmarkFill|Camera & Photos,Multicolor,Nature|0
bolt.badge.checkmark|SFBoltBadgeCheckmark|Camera & Photos,Multicolor,Nature|0
bolt.badge.clock.fill|SFBoltBadgeClockFill|Camera & Photos,Multicolor,Nature|0
bolt.badge.clock|SFBoltBadgeClock|Camera & Photos,Multicolor,Nature|0
bolt.badge.xmark.fill|SFBoltBadgeXmarkFill|Camera & Photos,Multicolor,Nature|0
bolt.badge.xmark|SFBoltBadgeXmark|Camera & Photos,Multicolor,Nature|0
bolt.batteryblock.fill|SFBoltBatteryblockFill|Automotive,Objects & Tools|0
bolt.batteryblock|SFBoltBatteryblock|Automotive,Objects & Tools|0
bolt.brakesignal|SFBoltBrakesignal|Automotive,Multicolor|0
bolt.car.circle.fill|SFBoltCarCircleFill|Automotive,Devices,Multicolor,Transportation|0
bolt.car.circle|SFBoltCarCircle|Automotive,Devices,Draw,Multicolor,Transportation,Variable|0
bolt.car.fill|SFBoltCarFill|Automotive,Devices,Multicolor,Transportation|0
bolt.car|SFBoltCar|Automotive,Devices,Multicolor,Transportation|0
bolt.circle.fill|SFBoltCircleFill|Camera & Photos,Multicolor,Nature|0
bolt.circle|SFBoltCircle|Camera & Photos,Draw,Multicolor,Nature,Variable|0
bolt.fill|SFBoltFill|Camera & Photos,Multicolor,Nature|0
bolt.heart.fill|SFBoltHeartFill|Health,Multicolor|0
bolt.heart|SFBoltHeart|Draw,Health,Variable|0
bolt.horizontal.circle.fill|SFBoltHorizontalCircleFill|Connectivity,Multicolor|0
bolt.horizontal.circle|SFBoltHorizontalCircle|Connectivity,Draw,Variable|0
bolt.horizontal.fill|SFBoltHorizontalFill|Connectivity|0
bolt.horizontal.icloud.fill|SFBoltHorizontalIcloudFill|Connectivity,Multicolor|1
bolt.horizontal.icloud|SFBoltHorizontalIcloud|Connectivity|1
bolt.horizontal|SFBoltHorizontal|Connectivity|0
bolt.house.fill|SFBoltHouseFill|Multicolor|0
bolt.house|SFBoltHouse||0
bolt.ring.closed|SFBoltRingClosed||0
bolt.shield.fill|SFBoltShieldFill|Multicolor,Nature,Objects & Tools|0
bolt.shield|SFBoltShield|Nature,Objects & Tools|0
bolt.slash.circle.fill|SFBoltSlashCircleFill|Camera & Photos,Multicolor,Nature|0
bolt.slash.circle|SFBoltSlashCircle|Camera & Photos,Draw,Multicolor,Nature,Variable|0
bolt.slash.fill|SFBoltSlashFill|Camera & Photos,Draw,Multicolor,Nature|0
bolt.slash|SFBoltSlash|Camera & Photos,Draw,Multicolor,Nature|0
bolt.square.fill|SFBoltSquareFill|Camera & Photos,Multicolor,Nature|0
bolt.square|SFBoltSquare|Camera & Photos,Draw,Multicolor,Nature|0
bolt|SFBolt|Camera & Photos,Multicolor,Nature|0
bolt.trianglebadge.exclamationmark.fill|SFBoltTrianglebadgeExclamationmarkFill|Camera & Photos,Multicolor,Nature|0
bolt.trianglebadge.exclamationmark|SFBoltTrianglebadgeExclamationmark|Camera & Photos,Multicolor,Nature|0
bonjour|SFBonjour|Connectivity,Multicolor|1
book.and.wrench.fill|SFBookAndWrenchFill|Automotive,Objects & Tools|0
book.and.wrench|SFBookAndWrench|Automotive,Objects & Tools|0
book.badge.plus.fill|SFBookBadgePlusFill|Multicolor,Objects & Tools|0
book.badge.plus|SFBookBadgePlus|Multicolor,Objects & Tools|0
book.circle.fill|SFBookCircleFill|Multicolor,Objects & Tools|0
book.circle|SFBookCircle|Draw,Objects & Tools,Variable|0
book.closed.circle.fill|SFBookClosedCircleFill|Multicolor,Objects & Tools|0
book.closed.circle|SFBookClosedCircle|Draw,Objects & Tools,Variable|0
book.closed.fill|SFBookClosedFill|Objects & Tools|0
book.closed|SFBookClosed|Objects & Tools|0
book.fill|SFBookFill|Objects & Tools|0
book.pages.fill|SFBookPagesFill|Multicolor|0
book.pages|SFBookPages||0
book|SFBook|Objects & Tools|0
bookmark.circle.fill|SFBookmarkCircleFill|Multicolor,Objects & Tools|0
bookmark.circle|SFBookmarkCircle|Draw,Multicolor,Objects & Tools,Variable|0
bookmark.fill|SFBookmarkFill|Multicolor,Objects & Tools|0
bookmark.slash.fill|SFBookmarkSlashFill|Draw,Multicolor,Objects & Tools|0
bookmark.slash|SFBookmarkSlash|Draw,Multicolor,Objects & Tools|0
bookmark.square.fill|SFBookmarkSquareFill|Multicolor,Objects & Tools|0
bookmark.square|SFBookmarkSquare|Draw,Multicolor,Objects & Tools|0
bookmark|SFBookmark|Multicolor,Objects & Tools|0
books.vertical.circle.fill|SFBooksVerticalCircleFill|Multicolor,Objects & Tools|0
books.vertical.circle|SFBooksVerticalCircle|Draw,Objects & Tools,Variable|0
books.vertical.fill|SFBooksVerticalFill|Objects & Tools|0
books.vertical|SFBooksVertical|Objects & Tools|0
brain.fill|SFBrainFill|Health,Human|0
brain.filled.head.profile|SFBrainFilledHeadProfile|Health,Human|0
brain.head.profile.fill|SFBrainHeadProfileFill|Health,Human|0
brain.head.profile|SFBrainHeadProfile|Health,Human|0
brain|SFBrain|Health,Human|0
brakesignal.dashed|SFBrakesignalDashed|Automotive|0
brakesignal|SFBrakesignal|Automotive|0
brazilianrealsign.arrow.trianglehead.counterclockwise.rotate.90|SFBrazilianrealsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
brazilianrealsign.bank.building.fill|SFBrazilianrealsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
brazilianrealsign.bank.building|SFBrazilianrealsignBankBuilding|Commerce,Objects & Tools|0
brazilianrealsign.circle.fill|SFBrazilianrealsignCircleFill|Commerce,Indices,Multicolor|0
brazilianrealsign.circle|SFBrazilianrealsignCircle|Commerce,Draw,Indices,Variable|0
brazilianrealsign.gauge.chart.lefthalf.righthalf|SFBrazilianrealsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
brazilianrealsign.gauge.chart.leftthird.topthird.rightthird|SFBrazilianrealsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
brazilianrealsign.ring.dashed|SFBrazilianrealsignRingDashed|Commerce,Home,Variable|0
brazilianrealsign.ring|SFBrazilianrealsignRing|Commerce,Draw,Home|0
brazilianrealsign.square.fill|SFBrazilianrealsignSquareFill|Commerce,Indices,Multicolor|0
brazilianrealsign.square|SFBrazilianrealsignSquare|Commerce,Draw,Indices|0
brazilianrealsign|SFBrazilianrealsign|Commerce,Indices|0
briefcase.circle.fill|SFBriefcaseCircleFill|Multicolor,Objects & Tools|0
briefcase.circle|SFBriefcaseCircle|Draw,Multicolor,Objects & Tools,Variable|0
briefcase.fill|SFBriefcaseFill|Multicolor,Objects & Tools|0
briefcase.sensor.tag.radiowaves.left.and.right.fill|SFBriefcaseSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
briefcase.sensor.tag.radiowaves.left.and.right|SFBriefcaseSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
briefcase|SFBriefcase|Multicolor,Objects & Tools|0
bubble.and.pencil|SFBubbleAndPencil|Communication,Editing|0
bubble.circle.fill|SFBubbleCircleFill|Communication,Multicolor|0
bubble.circle|SFBubbleCircle|Communication,Draw,Variable|0
bubble.fill|SFBubbleFill|Communication|0
bubble.left.and.bubble.right.fill|SFBubbleLeftAndBubbleRightFill|Communication|0
bubble.left.and.bubble.right|SFBubbleLeftAndBubbleRight|Communication|0
bubble.left.and.exclamationmark.bubble.right.fill|SFBubbleLeftAndExclamationmarkBubbleRightFill|Communication,Privacy & Security|0
bubble.left.and.exclamationmark.bubble.right|SFBubbleLeftAndExclamationmarkBubbleRight|Communication,Privacy & Security|0
bubble.left.and.text.bubble.right.fill|SFBubbleLeftAndTextBubbleRightFill|Communication|0
bubble.left.and.text.bubble.right|SFBubbleLeftAndTextBubbleRight|Communication|0
bubble.left.circle.fill|SFBubbleLeftCircleFill|Communication,Multicolor|0
bubble.left.circle|SFBubbleLeftCircle|Communication,Draw,Variable|0
bubble.left.fill|SFBubbleLeftFill|Communication|0
bubble.left|SFBubbleLeft|Communication|0
bubble.middle.bottom.fill|SFBubbleMiddleBottomFill|Communication|0
bubble.middle.bottom|SFBubbleMiddleBottom|Communication|0
bubble.middle.top.fill|SFBubbleMiddleTopFill|Communication|0
bubble.middle.top|SFBubbleMiddleTop|Communication|0
bubble.right.circle.fill|SFBubbleRightCircleFill|Communication,Multicolor|0
bubble.right.circle|SFBubbleRightCircle|Communication,Draw,Variable|0
bubble.right.fill|SFBubbleRightFill|Communication|0
bubble.right|SFBubbleRight|Communication|0
bubble|SFBubble|Communication|0
bubbles.and.sparkles.fill|SFBubblesAndSparklesFill|Health|0
bubbles.and.sparkles|SFBubblesAndSparkles|Health|0
building.2.crop.circle.fill|SFBuilding2CropCircleFill|Multicolor,Objects & Tools|0
building.2.crop.circle|SFBuilding2CropCircle|Objects & Tools|0
building.2.fill|SFBuilding2Fill|Objects & Tools|0
building.2|SFBuilding2|Objects & Tools|0
building.columns.circle.fill|SFBuildingColumnsCircleFill|Multicolor|0
building.columns.circle|SFBuildingColumnsCircle|Draw,Variable|0
building.columns.fill|SFBuildingColumnsFill||0
building.columns|SFBuildingColumns||0
building.fill|SFBuildingFill|Objects & Tools|0
building|SFBuilding|Objects & Tools|0
burn|SFBurn||0
burst.fill|SFBurstFill||0
burst|SFBurst||0
bus.doubledecker.fill|SFBusDoubledeckerFill|Maps,Transportation|0
bus.doubledecker|SFBusDoubledecker|Maps,Transportation|0
bus.fill|SFBusFill|Maps,Transportation|0
bus|SFBus|Maps,Transportation|0
button.angledbottom.horizontal.left.fill|SFButtonAngledbottomHorizontalLeftFill|Gaming,Shapes|0
button.angledbottom.horizontal.left|SFButtonAngledbottomHorizontalLeft|Gaming,Shapes|0
button.angledbottom.horizontal.right.fill|SFButtonAngledbottomHorizontalRightFill|Gaming,Shapes|0
button.angledbottom.horizontal.right|SFButtonAngledbottomHorizontalRight|Gaming,Shapes|0
button.angledtop.vertical.left.fill|SFButtonAngledtopVerticalLeftFill|Gaming,Shapes|0
button.angledtop.vertical.left|SFButtonAngledtopVerticalLeft|Gaming,Shapes|0
button.angledtop.vertical.right.fill|SFButtonAngledtopVerticalRightFill|Gaming,Shapes|0
button.angledtop.vertical.right|SFButtonAngledtopVerticalRight|Gaming,Shapes|0
button.horizontal.fill|SFButtonHorizontalFill|Gaming,Shapes|0
button.horizontal|SFButtonHorizontal|Gaming,Shapes|0
button.horizontal.top.fill|SFButtonHorizontalTopFill|Devices|0
button.horizontal.top.press.fill|SFButtonHorizontalTopPressFill|Devices|0
button.horizontal.top.press|SFButtonHorizontalTopPress|Devices,Draw|0
button.horizontal.top|SFButtonHorizontalTop|Devices|0
button.programmable.square.fill|SFButtonProgrammableSquareFill|Home,Multicolor|0
button.programmable.square|SFButtonProgrammableSquare|Draw,Home|0
button.programmable|SFButtonProgrammable|Home|0
button.roundedbottom.horizontal.fill|SFButtonRoundedbottomHorizontalFill|Gaming,Shapes|0
button.roundedbottom.horizontal|SFButtonRoundedbottomHorizontal|Gaming,Shapes|0
button.roundedtop.horizontal.fill|SFButtonRoundedtopHorizontalFill|Gaming,Shapes|0
button.roundedtop.horizontal|SFButtonRoundedtopHorizontal|Gaming,Shapes|0
button.vertical.left.fill|SFButtonVerticalLeftFill|Devices|0
button.vertical.left.press.fill|SFButtonVerticalLeftPressFill|Devices|0
button.vertical.left.press|SFButtonVerticalLeftPress|Devices,Draw|0
button.vertical.left|SFButtonVerticalLeft|Devices|0
button.vertical.right.fill|SFButtonVerticalRightFill|Devices|0
button.vertical.right.press.fill|SFButtonVerticalRightPressFill|Devices|0
button.vertical.right.press|SFButtonVerticalRightPress|Devices,Draw|0
button.vertical.right|SFButtonVerticalRight|Devices|0
c.circle.fill|SFCCircleFill|Gaming,Indices,Multicolor|0
c.circle|SFCCircle|Draw,Gaming,Indices,Variable|0
c.square.fill|SFCSquareFill|Indices,Multicolor|0
c.square|SFCSquare|Draw,Indices|0
cabinet.fill|SFCabinetFill|Home,Objects & Tools|0
cabinet|SFCabinet|Home,Objects & Tools|0
cable.coaxial|SFCableCoaxial|Devices,Draw|0
cable.connector.horizontal|SFCableConnectorHorizontal|Devices|0
cable.connector.slash|SFCableConnectorSlash|Devices|0
cable.connector|SFCableConnector|Devices|0
cable.connector.video|SFCableConnectorVideo|Devices,Draw|0
cablecar.fill|SFCablecarFill|Transportation|0
cablecar|SFCablecar|Transportation|0
calendar.and.person|SFCalendarAndPerson|Human,Multicolor,Objects & Tools|0
calendar.badge.checkmark|SFCalendarBadgeCheckmark|Multicolor,Objects & Tools|0
calendar.badge.clock|SFCalendarBadgeClock|Multicolor,Objects & Tools|0
calendar.badge.exclamationmark|SFCalendarBadgeExclamationmark|Multicolor,Objects & Tools|0
calendar.badge.lock|SFCalendarBadgeLock|Multicolor,Objects & Tools|0
calendar.badge.minus|SFCalendarBadgeMinus|Multicolor,Objects & Tools|0
calendar.badge.plus|SFCalendarBadgePlus|Multicolor,Objects & Tools|0
calendar.badge|SFCalendarBadge|Multicolor,Objects & Tools|0
calendar.circle.fill|SFCalendarCircleFill|Multicolor,Objects & Tools|0
calendar.circle|SFCalendarCircle|Draw,Multicolor,Objects & Tools,Variable|0
calendar.day.timeline.leading.circle.fill|SFCalendarDayTimelineLeadingCircleFill|Multicolor|0
calendar.day.timeline.leading.circle|SFCalendarDayTimelineLeadingCircle|Draw,Variable|0
calendar.day.timeline.leading|SFCalendarDayTimelineLeading|Multicolor|0
calendar.day.timeline.left.circle.fill|SFCalendarDayTimelineLeftCircleFill|Multicolor|0
calendar.day.timeline.left.circle|SFCalendarDayTimelineLeftCircle|Draw,Variable|0
calendar.day.timeline.left|SFCalendarDayTimelineLeft|Multicolor|0
calendar.day.timeline.right.circle.fill|SFCalendarDayTimelineRightCircleFill|Multicolor|0
calendar.day.timeline.right.circle|SFCalendarDayTimelineRightCircle|Draw,Variable|0
calendar.day.timeline.right|SFCalendarDayTimelineRight|Multicolor|0
calendar.day.timeline.trailing.circle.fill|SFCalendarDayTimelineTrailingCircleFill|Multicolor|0
calendar.day.timeline.trailing.circle|SFCalendarDayTimelineTrailingCircle|Draw,Variable|0
calendar.day.timeline.trailing|SFCalendarDayTimelineTrailing|Multicolor|0
calendar|SFCalendar|Multicolor,Objects & Tools|0
camera.aperture|SFCameraAperture|Camera & Photos|0
camera.badge.clock.fill|SFCameraBadgeClockFill|Camera & Photos,Multicolor,Objects & Tools|0
camera.badge.clock|SFCameraBadgeClock|Camera & Photos,Multicolor,Objects & Tools|0
camera.badge.ellipsis.fill|SFCameraBadgeEllipsisFill|Camera & Photos,Multicolor,Objects & Tools|0
camera.badge.ellipsis|SFCameraBadgeEllipsis|Camera & Photos,Multicolor,Objects & Tools|0
camera.circle.fill|SFCameraCircleFill|Camera & Photos,Multicolor,Objects & Tools|0
camera.circle|SFCameraCircle|Camera & Photos,Draw,Objects & Tools,Variable|0
camera.fill|SFCameraFill|Camera & Photos,Objects & Tools|0
camera.filters|SFCameraFilters|Camera & Photos,Draw,Editing,Objects & Tools|0
camera.macro.circle.fill|SFCameraMacroCircleFill|Camera & Photos,Multicolor,Nature|0
camera.macro.circle|SFCameraMacroCircle|Camera & Photos,Draw,Nature,Variable|0
camera.macro.slash.circle.fill|SFCameraMacroSlashCircleFill|Camera & Photos,Multicolor,Nature|0
camera.macro.slash.circle|SFCameraMacroSlashCircle|Camera & Photos,Draw,Nature,Variable|0
camera.macro.slash|SFCameraMacroSlash|Camera & Photos,Draw,Nature|0
camera.macro|SFCameraMacro|Camera & Photos,Nature|0
camera.metering.center.weighted.average|SFCameraMeteringCenterWeightedAverage|Camera & Photos|0
camera.metering.center.weighted|SFCameraMeteringCenterWeighted|Camera & Photos,Variable|0
camera.metering.matrix|SFCameraMeteringMatrix|Camera & Photos,Variable|0
camera.metering.multispot|SFCameraMeteringMultispot|Camera & Photos,Variable|0
camera.metering.none|SFCameraMeteringNone|Camera & Photos|0
camera.metering.partial|SFCameraMeteringPartial|Camera & Photos|0
camera.metering.spot|SFCameraMeteringSpot|Camera & Photos|0
camera.metering.unknown|SFCameraMeteringUnknown|Camera & Photos|0
camera.on.rectangle.fill|SFCameraOnRectangleFill|Camera & Photos,Objects & Tools|0
camera.on.rectangle|SFCameraOnRectangle|Camera & Photos,Objects & Tools|0
camera.sensor.tag.radiowaves.left.and.right.fill|SFCameraSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
camera.sensor.tag.radiowaves.left.and.right|SFCameraSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
camera.shutter.button.fill|SFCameraShutterButtonFill|Camera & Photos,Objects & Tools|0
camera.shutter.button|SFCameraShutterButton|Camera & Photos,Objects & Tools|0
camera|SFCamera|Camera & Photos,Objects & Tools|0
camera.viewfinder.badge.automatic|SFCameraViewfinderBadgeAutomatic|Camera & Photos,Multicolor,Objects & Tools|0
camera.viewfinder|SFCameraViewfinder|Camera & Photos,Objects & Tools|0
candybarphone|SFCandybarphone|Devices|1
capslock.fill|SFCapslockFill|Keyboard|0
capslock|SFCapslock|Keyboard|0
capsule.bottomhalf.filled|SFCapsuleBottomhalfFilled||0
capsule.fill|SFCapsuleFill|Shapes|0
capsule.lefthalf.filled|SFCapsuleLefthalfFilled||0
capsule.on.capsule.fill|SFCapsuleOnCapsuleFill||0
capsule.on.capsule|SFCapsuleOnCapsule||0
capsule.on.rectangle.fill|SFCapsuleOnRectangleFill||0
capsule.on.rectangle|SFCapsuleOnRectangle||0
capsule.portrait.bottomhalf.filled|SFCapsulePortraitBottomhalfFilled||0
capsule.portrait.fill|SFCapsulePortraitFill|Shapes|0
capsule.portrait.lefthalf.filled|SFCapsulePortraitLefthalfFilled||0
capsule.portrait.righthalf.filled|SFCapsulePortraitRighthalfFilled||0
capsule.portrait|SFCapsulePortrait|Shapes|0
capsule.portrait.tophalf.filled|SFCapsulePortraitTophalfFilled||0
capsule.righthalf.filled|SFCapsuleRighthalfFilled||0
capsule|SFCapsule|Shapes|0
capsule.tophalf.filled|SFCapsuleTophalfFilled||0
captions.bubble.fill|SFCaptionsBubbleFill|Accessibility,Communication|0
captions.bubble|SFCaptionsBubble|Accessibility,Communication|0
car.2.fill|SFCar2Fill|Automotive,Devices,Transportation|0
car.2|SFCar2|Automotive,Devices,Transportation|0
car.badge.gearshape.fill|SFCarBadgeGearshapeFill|Automotive,Devices,Transportation|0
car.badge.gearshape|SFCarBadgeGearshape|Automotive,Devices,Transportation|0
car.circle.fill|SFCarCircleFill|Automotive,Devices,Maps,Multicolor,Transportation|0
car.circle|SFCarCircle|Automotive,Devices,Draw,Maps,Multicolor,Transportation,Variable|0
car.ferry.fill|SFCarFerryFill|Transportation|0
car.ferry|SFCarFerry|Transportation|0
car.fill|SFCarFill|Automotive,Devices,Maps,Multicolor,Transportation|0
car.front.waves.down.fill|SFCarFrontWavesDownFill|Automotive,Devices,Draw,Transportation,Variable|0
car.front.waves.down|SFCarFrontWavesDown|Automotive,Devices,Draw,Transportation,Variable|0
car.front.waves.left.and.right.and.up.fill|SFCarFrontWavesLeftAndRightAndUpFill|Automotive,Devices,Draw,Transportation,Variable|0
car.front.waves.left.and.right.and.up|SFCarFrontWavesLeftAndRightAndUp|Automotive,Devices,Draw,Transportation,Variable|0
car.front.waves.up.fill|SFCarFrontWavesUpFill|Automotive,Devices,Draw,Transportation,Variable|0
car.front.waves.up|SFCarFrontWavesUp|Automotive,Devices,Draw,Transportation,Variable|0
car.rear.and.collision.road.lane.slash|SFCarRearAndCollisionRoadLaneSlash|Automotive,Multicolor|0
car.rear.and.collision.road.lane|SFCarRearAndCollisionRoadLane|Automotive,Multicolor|0
car.rear.and.tire.marks.off|SFCarRearAndTireMarksOff|Automotive|0
car.rear.and.tire.marks.slash|SFCarRearAndTireMarksSlash|Automotive,Draw,Multicolor|0
car.rear.and.tire.marks|SFCarRearAndTireMarks|Automotive,Multicolor|0
car.rear.fill|SFCarRearFill|Automotive,Devices,Transportation|0
car.rear.hazardsign.fill|SFCarRearHazardsignFill|Automotive,Multicolor|0
car.rear.hazardsign|SFCarRearHazardsign|Automotive|0
car.rear.road.lane.dashed.arrowtriangle.2.outward|SFCarRearRoadLaneDashedArrowtriangle2Outward|Automotive|0
car.rear.road.lane.dashed|SFCarRearRoadLaneDashed|Automotive|0
car.rear.road.lane.distance.1.and.gauge.open.with.lines.needle.67percent.and.arrowtriangle|SFCarRearRoadLaneDistance1AndGaugeOpenWithLinesNeedle67percentAndArrowtriangle|Automotive,Variable|0
car.rear.road.lane.distance.1|SFCarRearRoadLaneDistance1|Automotive,Variable|0
car.rear.road.lane.distance.2.and.gauge.open.with.lines.needle.67percent.and.arrowtriangle|SFCarRearRoadLaneDistance2AndGaugeOpenWithLinesNeedle67percentAndArrowtriangle|Automotive,Variable|0
car.rear.road.lane.distance.2|SFCarRearRoadLaneDistance2|Automotive,Variable|0
car.rear.road.lane.distance.3.and.gauge.open.with.lines.needle.67percent.and.arrowtriangle|SFCarRearRoadLaneDistance3AndGaugeOpenWithLinesNeedle67percentAndArrowtriangle|Automotive,Variable|0
car.rear.road.lane.distance.3|SFCarRearRoadLaneDistance3|Automotive,Variable|0
car.rear.road.lane.distance.4.and.gauge.open.with.lines.needle.67percent.and.arrowtriangle|SFCarRearRoadLaneDistance4AndGaugeOpenWithLinesNeedle67percentAndArrowtriangle|Automotive,Variable|0
car.rear.road.lane.distance.4|SFCarRearRoadLaneDistance4|Automotive,Variable|0
car.rear.road.lane.distance.5.and.gauge.open.with.lines.needle.67percent.and.arrowtriangle|SFCarRearRoadLaneDistance5AndGaugeOpenWithLinesNeedle67percentAndArrowtriangle|Automotive,Variable|0
car.rear.road.lane.distance.5|SFCarRearRoadLaneDistance5|Automotive,Variable|0
car.rear.road.lane.off|SFCarRearRoadLaneOff|Automotive|0
car.rear.road.lane|SFCarRearRoadLane|Automotive|0
car.rear.road.lane.wave.up|SFCarRearRoadLaneWaveUp|Automotive,Variable|0
car.rear|SFCarRear|Automotive,Devices,Transportation|0
car.rear.tilt.road.lanes.curved.right|SFCarRearTiltRoadLanesCurvedRight|Automotive|0
car.rear.waves.up.fill|SFCarRearWavesUpFill|Automotive,Draw,Variable|0
car.rear.waves.up|SFCarRearWavesUp|Automotive,Draw,Variable|0
car.side.air.circulate.fill|SFCarSideAirCirculateFill|Automotive,Multicolor|0
car.side.air.circulate|SFCarSideAirCirculate|Automotive|0
car.side.air.fresh.fill|SFCarSideAirFreshFill|Automotive|0
car.side.air.fresh|SFCarSideAirFresh|Automotive|0
car.side.and.exclamationmark.fill|SFCarSideAndExclamationmarkFill|Automotive,Multicolor|0
car.side.and.exclamationmark|SFCarSideAndExclamationmark|Automotive,Multicolor|0
car.side.arrow.left.and.right.fill|SFCarSideArrowLeftAndRightFill|Automotive,Draw|0
car.side.arrow.left.and.right|SFCarSideArrowLeftAndRight|Automotive,Draw|0
car.side.arrowtriangle.down.fill|SFCarSideArrowtriangleDownFill|Automotive|0
car.side.arrowtriangle.down|SFCarSideArrowtriangleDown|Automotive|0
car.side.arrowtriangle.up.arrowtriangle.down.fill|SFCarSideArrowtriangleUpArrowtriangleDownFill|Automotive|0
car.side.arrowtriangle.up.arrowtriangle.down|SFCarSideArrowtriangleUpArrowtriangleDown|Automotive|0
car.side.arrowtriangle.up.fill|SFCarSideArrowtriangleUpFill|Automotive|0
car.side.arrowtriangle.up|SFCarSideArrowtriangleUp|Automotive|0
car.side.fill|SFCarSideFill|Automotive|0
car.side.front.open.crop.fill|SFCarSideFrontOpenCropFill|Automotive|0
car.side.front.open.crop|SFCarSideFrontOpenCrop|Automotive|0
car.side.front.open.fill|SFCarSideFrontOpenFill|Automotive,Multicolor|0
car.side.front.open|SFCarSideFrontOpen|Automotive,Multicolor|0
car.side.hill.descent.control.fill|SFCarSideHillDescentControlFill|Automotive,Draw|0
car.side.hill.descent.control|SFCarSideHillDescentControl|Automotive,Draw|0
car.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle.fill|SFCarSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangleFill|Automotive,Draw|0
car.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle|SFCarSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangle|Automotive,Draw|0
car.side.hill.down.fill|SFCarSideHillDownFill|Automotive,Draw|0
car.side.hill.down|SFCarSideHillDown|Automotive,Draw|0
car.side.hill.up.fill|SFCarSideHillUpFill|Automotive,Draw|0
car.side.hill.up|SFCarSideHillUp|Automotive,Draw|0
car.side.lock.fill|SFCarSideLockFill|Automotive|0
car.side.lock.open.fill|SFCarSideLockOpenFill|Automotive|0
car.side.lock.open|SFCarSideLockOpen|Automotive|0
car.side.lock|SFCarSideLock|Automotive|0
car.side.rear.and.collision.and.car.side.front.and.arrow.forward|SFCarSideRearAndCollisionAndCarSideFrontAndArrowForward|Automotive,Draw|0
car.side.rear.and.collision.and.car.side.front.and.steeringwheel|SFCarSideRearAndCollisionAndCarSideFrontAndSteeringwheel|Automotive|0
car.side.rear.and.collision.and.car.side.front.slash|SFCarSideRearAndCollisionAndCarSideFrontSlash|Automotive|0
car.side.rear.and.collision.and.car.side.front|SFCarSideRearAndCollisionAndCarSideFront|Automotive|0
car.side.rear.and.exclamationmark.and.car.side.front.off|SFCarSideRearAndExclamationmarkAndCarSideFrontOff|Automotive|0
car.side.rear.and.exclamationmark.and.car.side.front|SFCarSideRearAndExclamationmarkAndCarSideFront|Automotive|0
car.side.rear.and.wave.3.and.car.side.front|SFCarSideRearAndWave3AndCarSideFront|Automotive,Variable|0
car.side.rear.crop.trunk.partition.fill|SFCarSideRearCropTrunkPartitionFill|Automotive|0
car.side.rear.crop.trunk.partition|SFCarSideRearCropTrunkPartition|Automotive|0
car.side.rear.open.crop.fill|SFCarSideRearOpenCropFill|Automotive|0
car.side.rear.open.crop|SFCarSideRearOpenCrop|Automotive|0
car.side.rear.open.fill|SFCarSideRearOpenFill|Automotive,Multicolor|0
car.side.rear.open|SFCarSideRearOpen|Automotive,Multicolor|0
car.side.rear.tow.hitch.fill|SFCarSideRearTowHitchFill|Automotive|0
car.side.rear.tow.hitch|SFCarSideRearTowHitch|Automotive|0
car.side.roof.cargo.carrier.fill|SFCarSideRoofCargoCarrierFill|Automotive|0
car.side.roof.cargo.carrier.slash.fill|SFCarSideRoofCargoCarrierSlashFill|Automotive|0
car.side.roof.cargo.carrier.slash|SFCarSideRoofCargoCarrierSlash|Automotive|0
car.side.roof.cargo.carrier|SFCarSideRoofCargoCarrier|Automotive|0
car.side|SFCarSide|Automotive|0
car|SFCar|Automotive,Devices,Maps,Multicolor,Transportation|0
car.top.arrowtriangle.front.left.fill|SFCarTopArrowtriangleFrontLeftFill|Automotive|0
car.top.arrowtriangle.front.left|SFCarTopArrowtriangleFrontLeft|Automotive|0
car.top.arrowtriangle.front.right.fill|SFCarTopArrowtriangleFrontRightFill|Automotive|0
car.top.arrowtriangle.front.right|SFCarTopArrowtriangleFrontRight|Automotive|0
car.top.arrowtriangle.rear.left.fill|SFCarTopArrowtriangleRearLeftFill|Automotive|0
car.top.arrowtriangle.rear.left|SFCarTopArrowtriangleRearLeft|Automotive|0
car.top.arrowtriangle.rear.right.fill|SFCarTopArrowtriangleRearRightFill|Automotive|0
car.top.arrowtriangle.rear.right|SFCarTopArrowtriangleRearRight|Automotive|0
car.top.door.front.left.and.front.right.and.rear.left.and.rear.right.open.fill|SFCarTopDoorFrontLeftAndFrontRightAndRearLeftAndRearRightOpenFill|Automotive,Multicolor|0
car.top.door.front.left.and.front.right.and.rear.left.and.rear.right.open|SFCarTopDoorFrontLeftAndFrontRightAndRearLeftAndRearRightOpen|Automotive,Multicolor|0
car.top.door.front.left.and.front.right.and.rear.left.open.fill|SFCarTopDoorFrontLeftAndFrontRightAndRearLeftOpenFill|Automotive,Multicolor|0
car.top.door.front.left.and.front.right.and.rear.left.open|SFCarTopDoorFrontLeftAndFrontRightAndRearLeftOpen|Automotive,Multicolor|0
car.top.door.front.left.and.front.right.and.rear.right.open.fill|SFCarTopDoorFrontLeftAndFrontRightAndRearRightOpenFill|Automotive,Multicolor|0
car.top.door.front.left.and.front.right.and.rear.right.open|SFCarTopDoorFrontLeftAndFrontRightAndRearRightOpen|Automotive,Multicolor|0
car.top.door.front.left.and.front.right.open.fill|SFCarTopDoorFrontLeftAndFrontRightOpenFill|Automotive,Multicolor|0
car.top.door.front.left.and.front.right.open|SFCarTopDoorFrontLeftAndFrontRightOpen|Automotive,Multicolor|0
car.top.door.front.left.and.rear.left.and.rear.right.open.fill|SFCarTopDoorFrontLeftAndRearLeftAndRearRightOpenFill|Automotive,Multicolor|0
car.top.door.front.left.and.rear.left.and.rear.right.open|SFCarTopDoorFrontLeftAndRearLeftAndRearRightOpen|Automotive,Multicolor|0
car.top.door.front.left.and.rear.left.open.fill|SFCarTopDoorFrontLeftAndRearLeftOpenFill|Automotive,Multicolor|0
car.top.door.front.left.and.rear.left.open|SFCarTopDoorFrontLeftAndRearLeftOpen|Automotive,Multicolor|0
car.top.door.front.left.and.rear.right.open.fill|SFCarTopDoorFrontLeftAndRearRightOpenFill|Automotive,Multicolor|0
car.top.door.front.left.and.rear.right.open|SFCarTopDoorFrontLeftAndRearRightOpen|Automotive,Multicolor|0
car.top.door.front.left.open.fill|SFCarTopDoorFrontLeftOpenFill|Automotive,Multicolor|0
car.top.door.front.left.open|SFCarTopDoorFrontLeftOpen|Automotive,Multicolor|0
car.top.door.front.right.and.rear.left.and.rear.right.open.fill|SFCarTopDoorFrontRightAndRearLeftAndRearRightOpenFill|Automotive,Multicolor|0
car.top.door.front.right.and.rear.left.and.rear.right.open|SFCarTopDoorFrontRightAndRearLeftAndRearRightOpen|Automotive,Multicolor|0
car.top.door.front.right.and.rear.left.open.fill|SFCarTopDoorFrontRightAndRearLeftOpenFill|Automotive,Multicolor|0
car.top.door.front.right.and.rear.left.open|SFCarTopDoorFrontRightAndRearLeftOpen|Automotive,Multicolor|0
car.top.door.front.right.and.rear.right.open.fill|SFCarTopDoorFrontRightAndRearRightOpenFill|Automotive,Multicolor|0
car.top.door.front.right.and.rear.right.open|SFCarTopDoorFrontRightAndRearRightOpen|Automotive,Multicolor|0
car.top.door.front.right.open.fill|SFCarTopDoorFrontRightOpenFill|Automotive,Multicolor|0
car.top.door.front.right.open|SFCarTopDoorFrontRightOpen|Automotive,Multicolor|0
car.top.door.rear.left.and.rear.right.open.fill|SFCarTopDoorRearLeftAndRearRightOpenFill|Automotive,Multicolor|0
car.top.door.rear.left.and.rear.right.open|SFCarTopDoorRearLeftAndRearRightOpen|Automotive,Multicolor|0
car.top.door.rear.left.open.fill|SFCarTopDoorRearLeftOpenFill|Automotive,Multicolor|0
car.top.door.rear.left.open|SFCarTopDoorRearLeftOpen|Automotive,Multicolor|0
car.top.door.rear.right.open.fill|SFCarTopDoorRearRightOpenFill|Automotive,Multicolor|0
car.top.door.rear.right.open|SFCarTopDoorRearRightOpen|Automotive,Multicolor|0
car.top.door.sliding.left.open.fill|SFCarTopDoorSlidingLeftOpenFill|Automotive,Multicolor|0
car.top.door.sliding.left.open|SFCarTopDoorSlidingLeftOpen|Automotive,Multicolor|0
car.top.door.sliding.right.open.fill|SFCarTopDoorSlidingRightOpenFill|Automotive,Multicolor|0
car.top.door.sliding.right.open|SFCarTopDoorSlidingRightOpen|Automotive,Multicolor|0
car.top.front.radiowaves.front.left.and.front.and.front.right.fill|SFCarTopFrontRadiowavesFrontLeftAndFrontAndFrontRightFill|Automotive,Variable|0
car.top.front.radiowaves.front.left.and.front.and.front.right|SFCarTopFrontRadiowavesFrontLeftAndFrontAndFrontRight|Automotive,Variable|0
car.top.lane.dashed.arrowtriangle.inward.fill|SFCarTopLaneDashedArrowtriangleInwardFill|Automotive|0
car.top.lane.dashed.arrowtriangle.inward|SFCarTopLaneDashedArrowtriangleInward|Automotive|0
car.top.lane.dashed.badge.steeringwheel.fill|SFCarTopLaneDashedBadgeSteeringwheelFill|Automotive|0
car.top.lane.dashed.badge.steeringwheel|SFCarTopLaneDashedBadgeSteeringwheel|Automotive|0
car.top.lane.dashed.departure.left.fill|SFCarTopLaneDashedDepartureLeftFill|Automotive|0
car.top.lane.dashed.departure.left.slash.fill|SFCarTopLaneDashedDepartureLeftSlashFill|Automotive|0
car.top.lane.dashed.departure.left.slash|SFCarTopLaneDashedDepartureLeftSlash|Automotive|0
car.top.lane.dashed.departure.left|SFCarTopLaneDashedDepartureLeft|Automotive|0
car.top.lane.dashed.departure.right.fill|SFCarTopLaneDashedDepartureRightFill|Automotive|0
car.top.lane.dashed.departure.right.slash.fill|SFCarTopLaneDashedDepartureRightSlashFill|Automotive|0
car.top.lane.dashed.departure.right.slash|SFCarTopLaneDashedDepartureRightSlash|Automotive|0
car.top.lane.dashed.departure.right|SFCarTopLaneDashedDepartureRight|Automotive|0
car.top.radiowaves.2.front.left.front.front.right.fill|SFCarTopRadiowaves2FrontLeftFrontFrontRightFill|Automotive,Variable|0
car.top.radiowaves.2.front.left.front.front.right|SFCarTopRadiowaves2FrontLeftFrontFrontRight|Automotive,Variable|0
car.top.radiowaves.2.rear.left.rear.rear.right.fill|SFCarTopRadiowaves2RearLeftRearRearRightFill|Automotive,Variable|0
car.top.radiowaves.2.rear.left.rear.rear.right|SFCarTopRadiowaves2RearLeftRearRearRight|Automotive,Variable|0
car.top.radiowaves.front.fill|SFCarTopRadiowavesFrontFill|Automotive,Variable|0
car.top.radiowaves.front|SFCarTopRadiowavesFront|Automotive,Variable|0
car.top.radiowaves.rear.fill|SFCarTopRadiowavesRearFill|Automotive,Variable|0
car.top.radiowaves.rear.left.and.rear.right.fill|SFCarTopRadiowavesRearLeftAndRearRightFill|Automotive,Variable|0
car.top.radiowaves.rear.left.and.rear.right|SFCarTopRadiowavesRearLeftAndRearRight|Automotive,Variable|0
car.top.radiowaves.rear.left.car.top.front.fill|SFCarTopRadiowavesRearLeftCarTopFrontFill|Automotive,Variable|0
car.top.radiowaves.rear.left.car.top.front|SFCarTopRadiowavesRearLeftCarTopFront|Automotive,Variable|0
car.top.radiowaves.rear.left.fill|SFCarTopRadiowavesRearLeftFill|Automotive,Variable|0
car.top.radiowaves.rear.left|SFCarTopRadiowavesRearLeft|Automotive,Variable|0
car.top.radiowaves.rear.right.badge.exclamationmark.fill|SFCarTopRadiowavesRearRightBadgeExclamationmarkFill|Automotive,Multicolor,Variable|0
car.top.radiowaves.rear.right.badge.exclamationmark|SFCarTopRadiowavesRearRightBadgeExclamationmark|Automotive,Multicolor,Variable|0
car.top.radiowaves.rear.right.badge.xmark.fill|SFCarTopRadiowavesRearRightBadgeXmarkFill|Automotive,Multicolor,Variable|0
car.top.radiowaves.rear.right.badge.xmark|SFCarTopRadiowavesRearRightBadgeXmark|Automotive,Multicolor,Variable|0
car.top.radiowaves.rear.right.car.top.front.fill|SFCarTopRadiowavesRearRightCarTopFrontFill|Automotive,Variable|0
car.top.radiowaves.rear.right.car.top.front|SFCarTopRadiowavesRearRightCarTopFront|Automotive,Variable|0
car.top.radiowaves.rear.right.fill|SFCarTopRadiowavesRearRightFill|Automotive,Variable|0
car.top.radiowaves.rear.right|SFCarTopRadiowavesRearRight|Automotive,Variable|0
car.top.radiowaves.rear|SFCarTopRadiowavesRear|Automotive,Variable|0
car.top.rear.radiowaves.rear.left.and.rear.and.rear.right.fill|SFCarTopRearRadiowavesRearLeftAndRearAndRearRightFill|Automotive,Variable|0
car.top.rear.radiowaves.rear.left.and.rear.and.rear.right|SFCarTopRearRadiowavesRearLeftAndRearAndRearRight|Automotive,Variable|0
car.top.video.rear.left.fill|SFCarTopVideoRearLeftFill|Automotive|0
car.top.video.rear.left|SFCarTopVideoRearLeft|Automotive|0
car.top.video.rear.right.fill|SFCarTopVideoRearRightFill|Automotive|0
car.top.video.rear.right|SFCarTopVideoRearRight|Automotive|0
car.window.left.badge.exclamationmark|SFCarWindowLeftBadgeExclamationmark|Automotive,Multicolor|0
car.window.left.badge.lock|SFCarWindowLeftBadgeLock|Automotive|0
car.window.left.badge.xmark|SFCarWindowLeftBadgeXmark|Automotive,Multicolor|0
car.window.left.exclamationmark|SFCarWindowLeftExclamationmark|Automotive|0
car.window.left|SFCarWindowLeft|Automotive|0
car.window.left.xmark|SFCarWindowLeftXmark|Automotive,Draw|0
car.window.right.badge.exclamationmark|SFCarWindowRightBadgeExclamationmark|Automotive,Multicolor|0
car.window.right.badge.lock|SFCarWindowRightBadgeLock|Automotive|0
car.window.right.badge.xmark|SFCarWindowRightBadgeXmark|Automotive,Multicolor|0
car.window.right.exclamationmark|SFCarWindowRightExclamationmark|Automotive|0
car.window.right|SFCarWindowRight|Automotive|0
car.window.right.xmark|SFCarWindowRightXmark|Automotive,Draw|0
carbon.dioxide.cloud.fill|SFCarbonDioxideCloudFill|Home,Weather|0
carbon.dioxide.cloud|SFCarbonDioxideCloud|Home,Weather|0
carbon.monoxide.cloud.fill|SFCarbonMonoxideCloudFill|Home,Weather|0
carbon.monoxide.cloud|SFCarbonMonoxideCloud|Home,Weather|0
carrot.fill|SFCarrotFill|Nature,Objects & Tools|0
carrot|SFCarrot|Nature,Objects & Tools|0
carseat.left.1.fill|SFCarseatLeft1Fill|Automotive|0
carseat.left.1|SFCarseatLeft1|Automotive|0
carseat.left.2.fill|SFCarseatLeft2Fill|Automotive|0
carseat.left.2|SFCarseatLeft2|Automotive|0
carseat.left.3.fill|SFCarseatLeft3Fill|Automotive|0
carseat.left.3|SFCarseatLeft3|Automotive|0
carseat.left.and.heat.waves.fill|SFCarseatLeftAndHeatWavesFill|Automotive|0
carseat.left.and.heat.waves|SFCarseatLeftAndHeatWaves|Automotive|0
carseat.left.backrest.up.and.down.fill|SFCarseatLeftBackrestUpAndDownFill|Automotive|0
carseat.left.backrest.up.and.down|SFCarseatLeftBackrestUpAndDown|Automotive|0
carseat.left.fan.fill|SFCarseatLeftFanFill|Automotive|0
carseat.left.fan|SFCarseatLeftFan|Automotive|0
carseat.left.fill|SFCarseatLeftFill|Automotive|0
carseat.left.forward.and.backward.fill|SFCarseatLeftForwardAndBackwardFill|Automotive|0
carseat.left.forward.and.backward|SFCarseatLeftForwardAndBackward|Automotive|0
carseat.left.massage.fill|SFCarseatLeftMassageFill|Automotive|0
carseat.left.massage|SFCarseatLeftMassage|Automotive|0
carseat.left|SFCarseatLeft|Automotive|0
carseat.left.up.and.down.fill|SFCarseatLeftUpAndDownFill|Automotive|0
carseat.left.up.and.down|SFCarseatLeftUpAndDown|Automotive|0
carseat.right.1.fill|SFCarseatRight1Fill|Automotive|0
carseat.right.1|SFCarseatRight1|Automotive|0
carseat.right.2.fill|SFCarseatRight2Fill|Automotive|0
carseat.right.2|SFCarseatRight2|Automotive|0
carseat.right.3.fill|SFCarseatRight3Fill|Automotive|0
carseat.right.3|SFCarseatRight3|Automotive|0
carseat.right.and.heat.waves.fill|SFCarseatRightAndHeatWavesFill|Automotive|0
carseat.right.and.heat.waves|SFCarseatRightAndHeatWaves|Automotive|0
carseat.right.backrest.up.and.down.fill|SFCarseatRightBackrestUpAndDownFill|Automotive|0
carseat.right.backrest.up.and.down|SFCarseatRightBackrestUpAndDown|Automotive|0
carseat.right.fan.fill|SFCarseatRightFanFill|Automotive|0
carseat.right.fan|SFCarseatRightFan|Automotive|0
carseat.right.fill|SFCarseatRightFill|Automotive|0
carseat.right.forward.and.backward.fill|SFCarseatRightForwardAndBackwardFill|Automotive|0
carseat.right.forward.and.backward|SFCarseatRightForwardAndBackward|Automotive|0
carseat.right.massage.fill|SFCarseatRightMassageFill|Automotive|0
carseat.right.massage|SFCarseatRightMassage|Automotive|0
carseat.right|SFCarseatRight|Automotive|0
carseat.right.up.and.down.fill|SFCarseatRightUpAndDownFill|Automotive|0
carseat.right.up.and.down|SFCarseatRightUpAndDown|Automotive|0
cart.badge.clock.fill|SFCartBadgeClockFill|Commerce,Multicolor,Objects & Tools|0
cart.badge.clock|SFCartBadgeClock|Commerce,Multicolor,Objects & Tools|0
cart.badge.minus|SFCartBadgeMinus|Commerce,Multicolor,Objects & Tools|0
cart.badge.plus|SFCartBadgePlus|Commerce,Multicolor,Objects & Tools|0
cart.badge.questionmark|SFCartBadgeQuestionmark|Commerce,Multicolor,Objects & Tools|0
cart.circle.fill|SFCartCircleFill|Commerce,Multicolor,Objects & Tools|0
cart.circle|SFCartCircle|Commerce,Draw,Objects & Tools,Variable|0
cart.fill.badge.minus|SFCartFillBadgeMinus|Commerce,Multicolor,Objects & Tools|0
cart.fill.badge.plus|SFCartFillBadgePlus|Commerce,Multicolor,Objects & Tools|0
cart.fill.badge.questionmark|SFCartFillBadgeQuestionmark|Commerce,Multicolor,Objects & Tools|0
cart.fill|SFCartFill|Commerce,Objects & Tools|0
cart|SFCart|Commerce,Objects & Tools|0
case.fill|SFCaseFill|Objects & Tools|0
case|SFCase|Objects & Tools|0
cat.circle.fill|SFCatCircleFill|Multicolor,Nature|0
cat.circle|SFCatCircle|Draw,Nature,Variable|0
cat.fill|SFCatFill|Nature|0
cat|SFCat|Nature|0
cedisign.arrow.trianglehead.counterclockwise.rotate.90|SFCedisignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
cedisign.bank.building.fill|SFCedisignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
cedisign.bank.building|SFCedisignBankBuilding|Commerce,Objects & Tools|0
cedisign.circle.fill|SFCedisignCircleFill|Commerce,Indices,Multicolor|0
cedisign.circle|SFCedisignCircle|Commerce,Draw,Indices,Variable|0
cedisign.gauge.chart.lefthalf.righthalf|SFCedisignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
cedisign.gauge.chart.leftthird.topthird.rightthird|SFCedisignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
cedisign.ring.dashed|SFCedisignRingDashed|Commerce,Home,Variable|0
cedisign.ring|SFCedisignRing|Commerce,Draw,Home|0
cedisign.square.fill|SFCedisignSquareFill|Commerce,Indices,Multicolor|0
cedisign.square|SFCedisignSquare|Commerce,Draw,Indices|0
cedisign|SFCedisign|Commerce,Indices|0
cellularbars.circle.fill|SFCellularbarsCircleFill|Connectivity,Draw,Multicolor,Variable|0
cellularbars.circle|SFCellularbarsCircle|Connectivity,Draw,Variable|0
cellularbars|SFCellularbars|Connectivity,Draw,Variable|0
centsign.arrow.trianglehead.counterclockwise.rotate.90|SFCentsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
centsign.bank.building.fill|SFCentsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
centsign.bank.building|SFCentsignBankBuilding|Commerce,Objects & Tools|0
centsign.circle.fill|SFCentsignCircleFill|Commerce,Indices,Multicolor|0
centsign.circle|SFCentsignCircle|Commerce,Draw,Indices,Variable|0
centsign.gauge.chart.lefthalf.righthalf|SFCentsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
centsign.gauge.chart.leftthird.topthird.rightthird|SFCentsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
centsign.ring.dashed|SFCentsignRingDashed|Commerce,Home,Variable|0
centsign.ring|SFCentsignRing|Commerce,Draw,Home|0
centsign.square.fill|SFCentsignSquareFill|Commerce,Indices,Multicolor|0
centsign.square|SFCentsignSquare|Commerce,Draw,Indices|0
centsign|SFCentsign|Commerce,Indices|0
chair.fill|SFChairFill|Home,Objects & Tools|0
chair.lounge.fill|SFChairLoungeFill|Home,Objects & Tools|0
chair.lounge|SFChairLounge|Home,Objects & Tools|0
chair|SFChair|Home,Objects & Tools|0
chandelier.fill|SFChandelierFill|Home,Objects & Tools|0
chandelier|SFChandelier|Home,Objects & Tools|0
character.book.closed.fill|SFCharacterBookClosedFill|Objects & Tools|0
character.book.closed|SFCharacterBookClosed|Objects & Tools|0
character.bubble.fill|SFCharacterBubbleFill|Communication,Multicolor|0
character.bubble|SFCharacterBubble|Communication|0
character.circle.fill|SFCharacterCircleFill|Multicolor,Text Formatting|0
character.circle|SFCharacterCircle|Draw,Text Formatting,Variable|0
character.cursor.ibeam|SFCharacterCursorIbeam|Text Formatting|0
character.duployan|SFCharacterDuployan|Accessibility,Text Formatting|0
character.magnify|SFCharacterMagnify|Accessibility,Text Formatting|0
character.phonetic|SFCharacterPhonetic|Text Formatting|0
character.square.fill|SFCharacterSquareFill|Multicolor,Text Formatting|0
character.square|SFCharacterSquare|Draw,Text Formatting|0
character.sutton|SFCharacterSutton|Text Formatting|0
character|SFCharacter|Text Formatting|0
character.text.justify|SFCharacterTextJustify|Draw,Text Formatting|0
character.textbox.badge.sparkles|SFCharacterTextboxBadgeSparkles|Text Formatting|0
character.textbox|SFCharacterTextbox|Text Formatting|0
characters.lowercase|SFCharactersLowercase|Text Formatting|0
characters.uppercase|SFCharactersUppercase|Text Formatting|0
chart.bar.fill|SFChartBarFill|Connectivity,Draw,Variable|0
chart.bar.horizontal.page.fill|SFChartBarHorizontalPageFill|Multicolor,Variable|0
chart.bar.horizontal.page|SFChartBarHorizontalPage|Variable|0
chart.bar|SFChartBar|Connectivity,Variable|0
chart.bar.xaxis.ascending.badge.clock|SFChartBarXaxisAscendingBadgeClock|Multicolor,Variable|0
chart.bar.xaxis.ascending|SFChartBarXaxisAscending|Draw,Variable|0
chart.bar.xaxis.descending|SFChartBarXaxisDescending|Draw,Variable|0
chart.bar.xaxis|SFChartBarXaxis|Draw,Variable|0
chart.bar.yaxis|SFChartBarYaxis|Draw,Variable|0
chart.dots.scatter|SFChartDotsScatter|Variable|0
chart.line.downtrend.xyaxis.circle.fill|SFChartLineDowntrendXyaxisCircleFill|Multicolor|0
chart.line.downtrend.xyaxis.circle|SFChartLineDowntrendXyaxisCircle|Draw,Variable|0
chart.line.downtrend.xyaxis|SFChartLineDowntrendXyaxis|Draw|0
chart.line.flattrend.xyaxis.circle.fill|SFChartLineFlattrendXyaxisCircleFill|Multicolor|0
chart.line.flattrend.xyaxis.circle|SFChartLineFlattrendXyaxisCircle|Draw,Variable|0
chart.line.flattrend.xyaxis|SFChartLineFlattrendXyaxis|Draw|0
chart.line.text.clipboard.fill|SFChartLineTextClipboardFill|Health,Objects & Tools|0
chart.line.text.clipboard|SFChartLineTextClipboard|Health,Objects & Tools|0
chart.line.uptrend.xyaxis.circle.fill|SFChartLineUptrendXyaxisCircleFill|Multicolor|0
chart.line.uptrend.xyaxis.circle|SFChartLineUptrendXyaxisCircle|Draw,Variable|0
chart.line.uptrend.xyaxis|SFChartLineUptrendXyaxis|Draw|0
chart.pie.fill|SFChartPieFill||0
chart.pie|SFChartPie||0
chart.xyaxis.line|SFChartXyaxisLine|Draw,Objects & Tools|0
checklist.checked|SFChecklistChecked|Draw,Text Formatting|0
checklist|SFChecklist|Draw,Text Formatting|0
checklist.unchecked|SFChecklistUnchecked|Draw,Text Formatting|0
checkmark.app.fill|SFCheckmarkAppFill|Draw,Multicolor|0
checkmark.app|SFCheckmarkApp|Draw|0
checkmark.applewatch|SFCheckmarkApplewatch|Devices|1
checkmark.arrow.trianglehead.clockwise|SFCheckmarkArrowTriangleheadClockwise|Draw|0
checkmark.arrow.trianglehead.counterclockwise|SFCheckmarkArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
checkmark.bubble.fill|SFCheckmarkBubbleFill|Communication,Draw|0
checkmark.bubble|SFCheckmarkBubble|Communication,Draw|0
checkmark.circle.badge.airplane.fill|SFCheckmarkCircleBadgeAirplaneFill|Maps,Transportation|0
checkmark.circle.badge.airplane|SFCheckmarkCircleBadgeAirplane|Maps,Transportation|0
checkmark.circle.badge.plus.fill|SFCheckmarkCircleBadgePlusFill|Multicolor,Privacy & Security|0
checkmark.circle.badge.plus|SFCheckmarkCircleBadgePlus|Multicolor,Privacy & Security,Variable|0
checkmark.circle.badge.questionmark.fill|SFCheckmarkCircleBadgeQuestionmarkFill|Multicolor,Privacy & Security|0
checkmark.circle.badge.questionmark|SFCheckmarkCircleBadgeQuestionmark|Multicolor,Privacy & Security,Variable|0
checkmark.circle.badge.xmark.fill|SFCheckmarkCircleBadgeXmarkFill|Multicolor,Privacy & Security|0
checkmark.circle.badge.xmark|SFCheckmarkCircleBadgeXmark|Multicolor,Privacy & Security,Variable|0
checkmark.circle.dotted|SFCheckmarkCircleDotted|Draw,Privacy & Security|0
checkmark.circle.fill|SFCheckmarkCircleFill|Draw,Multicolor,Privacy & Security|0
checkmark.circle|SFCheckmarkCircle|Draw,Multicolor,Privacy & Security,Variable|0
checkmark.circle.trianglebadge.exclamationmark.fill|SFCheckmarkCircleTrianglebadgeExclamationmarkFill|Multicolor,Privacy & Security|0
checkmark.circle.trianglebadge.exclamationmark|SFCheckmarkCircleTrianglebadgeExclamationmark|Multicolor,Privacy & Security,Variable|0
checkmark.diamond.fill|SFCheckmarkDiamondFill|Draw,Multicolor,Privacy & Security|0
checkmark.diamond|SFCheckmarkDiamond|Draw,Multicolor,Privacy & Security|0
checkmark.icloud.fill|SFCheckmarkIcloudFill|Connectivity,Draw,Multicolor,Privacy & Security|1
checkmark.icloud|SFCheckmarkIcloud|Connectivity,Draw,Privacy & Security|1
checkmark.message.fill|SFCheckmarkMessageFill|Communication,Draw,Multicolor,Privacy & Security|1
checkmark.message|SFCheckmarkMessage|Communication,Draw,Privacy & Security|1
checkmark.rectangle.fill|SFCheckmarkRectangleFill|Draw,Multicolor,Privacy & Security|0
checkmark.rectangle.portrait.fill|SFCheckmarkRectanglePortraitFill|Draw,Multicolor,Privacy & Security|0
checkmark.rectangle.portrait|SFCheckmarkRectanglePortrait|Draw,Privacy & Security|0
checkmark.rectangle.stack.fill|SFCheckmarkRectangleStackFill|Multicolor|0
checkmark.rectangle.stack|SFCheckmarkRectangleStack||0
checkmark.rectangle|SFCheckmarkRectangle|Draw,Privacy & Security|0
checkmark.seal.fill|SFCheckmarkSealFill|Draw,Multicolor,Privacy & Security|0
checkmark.seal|SFCheckmarkSeal|Draw,Privacy & Security|0
checkmark.seal.text.page.fill|SFCheckmarkSealTextPageFill||0
checkmark.seal.text.page|SFCheckmarkSealTextPage||0
checkmark.shield.fill|SFCheckmarkShieldFill|Draw,Multicolor,Objects & Tools,Privacy & Security|0
checkmark.shield|SFCheckmarkShield|Draw,Objects & Tools,Privacy & Security|0
checkmark.square.fill|SFCheckmarkSquareFill|Draw,Multicolor,Privacy & Security|0
checkmark.square|SFCheckmarkSquare|Draw,Multicolor,Privacy & Security|0
checkmark|SFCheckmark|Draw,Multicolor,Privacy & Security|0
chevron.backward.2|SFChevronBackward2|Arrows|0
chevron.backward.chevron.backward.dotted|SFChevronBackwardChevronBackwardDotted|Arrows|0
chevron.backward.circle.fill|SFChevronBackwardCircleFill|Arrows,Camera & Photos,Multicolor|0
chevron.backward.circle|SFChevronBackwardCircle|Arrows,Camera & Photos,Draw,Variable|0
chevron.backward.square.fill|SFChevronBackwardSquareFill|Arrows,Multicolor|0
chevron.backward.square|SFChevronBackwardSquare|Arrows,Draw|0
chevron.backward|SFChevronBackward|Arrows,Camera & Photos|0
chevron.backward.to.line|SFChevronBackwardToLine|Keyboard|0
chevron.compact.backward|SFChevronCompactBackward|Arrows|0
chevron.compact.down|SFChevronCompactDown|Arrows|0
chevron.compact.forward|SFChevronCompactForward|Arrows|0
chevron.compact.left.chevron.compact.right|SFChevronCompactLeftChevronCompactRight|Arrows|0
chevron.compact.left|SFChevronCompactLeft|Arrows|0
chevron.compact.right|SFChevronCompactRight|Arrows|0
chevron.compact.up.chevron.compact.down|SFChevronCompactUpChevronCompactDown|Arrows|0
chevron.compact.up.chevron.compact.right.chevron.compact.down.chevron.compact.left|SFChevronCompactUpChevronCompactRightChevronCompactDownChevronCompactLeft|Arrows|0
chevron.compact.up|SFChevronCompactUp|Arrows|0
chevron.down.2|SFChevronDown2|Arrows,Fitness|0
chevron.down.circle.fill|SFChevronDownCircleFill|Arrows,Camera & Photos,Multicolor|0
chevron.down.circle|SFChevronDownCircle|Arrows,Camera & Photos,Draw,Variable|0
chevron.down.dotted.2|SFChevronDownDotted2|Arrows|0
chevron.down.forward.2|SFChevronDownForward2|Arrows|0
chevron.down.forward.dotted.2|SFChevronDownForwardDotted2|Arrows,Fitness|0
chevron.down.right.2|SFChevronDownRight2|Arrows|0
chevron.down.right.dotted.2|SFChevronDownRightDotted2|Arrows,Fitness|0
chevron.down.square.fill|SFChevronDownSquareFill|Arrows,Multicolor|0
chevron.down.square|SFChevronDownSquare|Arrows,Draw|0
chevron.down|SFChevronDown|Arrows,Camera & Photos|0
chevron.forward.2|SFChevronForward2|Arrows|0
chevron.forward.circle.fill|SFChevronForwardCircleFill|Arrows,Camera & Photos,Multicolor|0
chevron.forward.circle|SFChevronForwardCircle|Arrows,Camera & Photos,Draw,Variable|0
chevron.forward.dotted.chevron.forward|SFChevronForwardDottedChevronForward|Arrows|0
chevron.forward.square.fill|SFChevronForwardSquareFill|Arrows,Multicolor|0
chevron.forward.square|SFChevronForwardSquare|Arrows,Draw|0
chevron.forward|SFChevronForward|Arrows,Camera & Photos|0
chevron.forward.to.line|SFChevronForwardToLine|Keyboard|0
chevron.left.2|SFChevronLeft2|Arrows|0
chevron.left.chevron.left.dotted|SFChevronLeftChevronLeftDotted|Arrows|0
chevron.left.chevron.right|SFChevronLeftChevronRight|Arrows|0
chevron.left.circle.fill|SFChevronLeftCircleFill|Arrows,Camera & Photos,Multicolor|0
chevron.left.circle|SFChevronLeftCircle|Arrows,Camera & Photos,Draw,Variable|0
chevron.left.forwardslash.chevron.right|SFChevronLeftForwardslashChevronRight||0
chevron.left.square.fill|SFChevronLeftSquareFill|Arrows,Multicolor|0
chevron.left.square|SFChevronLeftSquare|Arrows,Draw|0
chevron.left|SFChevronLeft|Arrows,Camera & Photos|0
chevron.left.to.line|SFChevronLeftToLine|Keyboard|0
chevron.right.2|SFChevronRight2|Arrows|0
chevron.right.circle.fill|SFChevronRightCircleFill|Arrows,Camera & Photos,Multicolor|0
chevron.right.circle|SFChevronRightCircle|Arrows,Camera & Photos,Draw,Variable|0
chevron.right.dotted.chevron.right|SFChevronRightDottedChevronRight|Arrows|0
chevron.right.square.fill|SFChevronRightSquareFill|Arrows,Multicolor|0
chevron.right.square|SFChevronRightSquare|Arrows,Draw|0
chevron.right|SFChevronRight|Arrows,Camera & Photos|0
chevron.right.to.line|SFChevronRightToLine|Keyboard|0
chevron.up.2|SFChevronUp2|Arrows,Fitness|0
chevron.up.chevron.down.square.fill|SFChevronUpChevronDownSquareFill|Arrows,Multicolor|0
chevron.up.chevron.down.square|SFChevronUpChevronDownSquare|Arrows,Draw|0
chevron.up.chevron.down|SFChevronUpChevronDown|Arrows|0
chevron.up.chevron.right.chevron.down.chevron.left|SFChevronUpChevronRightChevronDownChevronLeft|Arrows|0
chevron.up.circle.fill|SFChevronUpCircleFill|Arrows,Camera & Photos,Multicolor|0
chevron.up.circle|SFChevronUpCircle|Arrows,Camera & Photos,Draw,Variable|0
chevron.up.dotted.2|SFChevronUpDotted2|Arrows|0
chevron.up.forward.2|SFChevronUpForward2|Arrows|0
chevron.up.forward.dotted.2|SFChevronUpForwardDotted2|Arrows,Fitness|0
chevron.up.right.2|SFChevronUpRight2|Arrows|0
chevron.up.right.dotted.2|SFChevronUpRightDotted2|Arrows,Fitness|0
chevron.up.square.fill|SFChevronUpSquareFill|Arrows,Multicolor|0
chevron.up.square|SFChevronUpSquare|Arrows,Draw|0
chevron.up|SFChevronUp|Arrows,Camera & Photos|0
chineseyuanrenminbisign.arrow.trianglehead.counterclockwise.rotate.90|SFChineseyuanrenminbisignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
chineseyuanrenminbisign.bank.building.fill|SFChineseyuanrenminbisignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
chineseyuanrenminbisign.bank.building|SFChineseyuanrenminbisignBankBuilding|Commerce,Objects & Tools|0
chineseyuanrenminbisign.circle.fill|SFChineseyuanrenminbisignCircleFill|Commerce,Indices,Multicolor|0
chineseyuanrenminbisign.circle|SFChineseyuanrenminbisignCircle|Commerce,Draw,Indices,Variable|0
chineseyuanrenminbisign.gauge.chart.lefthalf.righthalf|SFChineseyuanrenminbisignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
chineseyuanrenminbisign.gauge.chart.leftthird.topthird.rightthird|SFChineseyuanrenminbisignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
chineseyuanrenminbisign.ring.dashed|SFChineseyuanrenminbisignRingDashed|Commerce,Home,Variable|0
chineseyuanrenminbisign.ring|SFChineseyuanrenminbisignRing|Commerce,Draw,Home|0
chineseyuanrenminbisign.square.fill|SFChineseyuanrenminbisignSquareFill|Commerce,Indices,Multicolor|0
chineseyuanrenminbisign.square|SFChineseyuanrenminbisignSquare|Commerce,Draw,Indices|0
chineseyuanrenminbisign|SFChineseyuanrenminbisign|Commerce,Indices|0
circle.and.line.horizontal.fill|SFCircleAndLineHorizontalFill|Camera & Photos,Editing|0
circle.and.line.horizontal|SFCircleAndLineHorizontal|Camera & Photos,Draw,Editing|0
circle.badge.checkmark.fill|SFCircleBadgeCheckmarkFill|Multicolor|0
circle.badge.checkmark|SFCircleBadgeCheckmark|Multicolor,Variable|0
circle.badge.exclamationmark.fill|SFCircleBadgeExclamationmarkFill|Multicolor|0
circle.badge.exclamationmark|SFCircleBadgeExclamationmark|Multicolor,Variable|0
circle.badge.minus.fill|SFCircleBadgeMinusFill|Multicolor|0
circle.badge.minus|SFCircleBadgeMinus|Multicolor,Variable|0
circle.badge.plus.fill|SFCircleBadgePlusFill|Multicolor|0
circle.badge.plus|SFCircleBadgePlus|Multicolor,Variable|0
circle.badge.questionmark.fill|SFCircleBadgeQuestionmarkFill|Multicolor|0
circle.badge.questionmark|SFCircleBadgeQuestionmark|Multicolor,Variable|0
circle.badge.xmark.fill|SFCircleBadgeXmarkFill|Multicolor|0
circle.badge.xmark|SFCircleBadgeXmark|Multicolor,Variable|0
circle.bottomhalf.filled.inverse|SFCircleBottomhalfFilledInverse||0
circle.bottomhalf.filled|SFCircleBottomhalfFilled||0
circle.bottomrighthalf.pattern.checkered|SFCircleBottomrighthalfPatternCheckered|Camera & Photos|0
circle.circle.fill|SFCircleCircleFill|Gaming,Multicolor|0
circle.circle|SFCircleCircle|Draw,Gaming,Variable|0
circle.dashed.rectangle|SFCircleDashedRectangle|Camera & Photos,Draw|0
circle.dashed|SFCircleDashed|Editing|0
circle.dotted.and.circle|SFCircleDottedAndCircle|Camera & Photos|0
circle.dotted.circle.fill|SFCircleDottedCircleFill|Camera & Photos,Multicolor|0
circle.dotted.circle|SFCircleDottedCircle|Camera & Photos,Draw,Variable|0
circle.dotted|SFCircleDotted||0
circle.fill|SFCircleFill|Shapes|0
circle.filled.ipad.fill|SFCircleFilledIpadFill|Devices,Multicolor|0
circle.filled.ipad.landscape.fill|SFCircleFilledIpadLandscapeFill|Devices,Multicolor|0
circle.filled.ipad.landscape|SFCircleFilledIpadLandscape|Devices|0
circle.filled.ipad|SFCircleFilledIpad|Devices|0
circle.filled.iphone.fill|SFCircleFilledIphoneFill|Devices,Multicolor|0
circle.filled.iphone|SFCircleFilledIphone|Devices|0
circle.filled.pattern.diagonalline.rectangle|SFCircleFilledPatternDiagonallineRectangle|Camera & Photos|0
circle.grid.2x1.fill|SFCircleGrid2x1Fill||0
circle.grid.2x1.left.filled|SFCircleGrid2x1LeftFilled||0
circle.grid.2x1.right.filled|SFCircleGrid2x1RightFilled||0
circle.grid.2x1|SFCircleGrid2x1||0
circle.grid.2x2.fill|SFCircleGrid2x2Fill||0
circle.grid.2x2|SFCircleGrid2x2||0
circle.grid.2x2.topleft.checkmark.filled|SFCircleGrid2x2TopleftCheckmarkFilled|Multicolor|0
circle.grid.3x3.circle.fill|SFCircleGrid3x3CircleFill|Multicolor|0
circle.grid.3x3.circle|SFCircleGrid3x3Circle|Draw,Variable|0
circle.grid.3x3.fill|SFCircleGrid3x3Fill||0
circle.grid.3x3|SFCircleGrid3x3||0
circle.grid.cross.down.filled|SFCircleGridCrossDownFilled|Gaming|0
circle.grid.cross.fill|SFCircleGridCrossFill|Gaming|0
circle.grid.cross.left.filled|SFCircleGridCrossLeftFilled|Gaming|0
circle.grid.cross.right.filled|SFCircleGridCrossRightFilled|Gaming|0
circle.grid.cross|SFCircleGridCross|Gaming|0
circle.grid.cross.up.filled|SFCircleGridCrossUpFilled|Gaming|0
circle.hexagongrid.circle.fill|SFCircleHexagongridCircleFill|Multicolor|0
circle.hexagongrid.circle|SFCircleHexagongridCircle|Draw,Variable|0
circle.hexagongrid.fill|SFCircleHexagongridFill|Multicolor|0
circle.hexagongrid|SFCircleHexagongrid||0
circle.hexagonpath.fill|SFCircleHexagonpathFill|Accessibility|0
circle.hexagonpath|SFCircleHexagonpath|Accessibility|0
circle.lefthalf.filled.inverse|SFCircleLefthalfFilledInverse|Editing|0
circle.lefthalf.filled.righthalf.striped.horizontal.inverse|SFCircleLefthalfFilledRighthalfStripedHorizontalInverse|Camera & Photos|0
circle.lefthalf.filled.righthalf.striped.horizontal|SFCircleLefthalfFilledRighthalfStripedHorizontal|Camera & Photos|0
circle.lefthalf.filled|SFCircleLefthalfFilled|Editing|0
circle.lefthalf.striped.horizontal.inverse|SFCircleLefthalfStripedHorizontalInverse|Camera & Photos|0
circle.lefthalf.striped.horizontal|SFCircleLefthalfStripedHorizontal|Camera & Photos|0
circle.on.square.intersection.dotted|SFCircleOnSquareIntersectionDotted||0
circle.on.square.merge|SFCircleOnSquareMerge||0
circle.on.square|SFCircleOnSquare||0
circle.rectangle.dashed|SFCircleRectangleDashed|Camera & Photos|0
circle.rectangle.filled.pattern.diagonalline|SFCircleRectangleFilledPatternDiagonalline|Camera & Photos|0
circle.righthalf.filled.inverse|SFCircleRighthalfFilledInverse|Editing|0
circle.righthalf.filled|SFCircleRighthalfFilled|Editing|0
circle.slash.fill|SFCircleSlashFill|Draw|0
circle.slash|SFCircleSlash|Draw|0
circle.square.fill|SFCircleSquareFill|Gaming,Multicolor|0
circle.square|SFCircleSquare|Draw,Gaming|0
circle|SFCircle|Draw,Shapes,Variable|0
circle.tophalf.filled.inverse|SFCircleTophalfFilledInverse||0
circle.tophalf.filled|SFCircleTophalfFilled||0
circlebadge.2.fill|SFCirclebadge2Fill||0
circlebadge.2|SFCirclebadge2||0
circlebadge.fill|SFCirclebadgeFill|Multicolor|0
circlebadge|SFCirclebadge||0
clear.fill|SFClearFill|Keyboard,Multicolor|0
clear|SFClear|Draw,Keyboard,Multicolor|0
clipboard.fill|SFClipboardFill|Objects & Tools|0
clipboard|SFClipboard|Objects & Tools|0
clock.arrow.trianglehead.2.counterclockwise.rotate.90|SFClockArrowTrianglehead2CounterclockwiseRotate90|Arrows,Draw,Time|0
clock.arrow.trianglehead.clockwise.rotate.90.path.dotted|SFClockArrowTriangleheadClockwiseRotate90PathDotted|Objects & Tools,Time|0
clock.arrow.trianglehead.counterclockwise.rotate.90|SFClockArrowTriangleheadCounterclockwiseRotate90|Arrows,Draw,Time|0
clock.badge.airplane.fill|SFClockBadgeAirplaneFill|Objects & Tools,Time|0
clock.badge.airplane|SFClockBadgeAirplane|Objects & Tools,Time|0
clock.badge.checkmark.fill|SFClockBadgeCheckmarkFill|Multicolor,Objects & Tools,Time|0
clock.badge.checkmark|SFClockBadgeCheckmark|Multicolor,Objects & Tools,Time|0
clock.badge.exclamationmark.fill|SFClockBadgeExclamationmarkFill|Multicolor,Objects & Tools,Time|0
clock.badge.exclamationmark|SFClockBadgeExclamationmark|Multicolor,Objects & Tools,Time|0
clock.badge.fill|SFClockBadgeFill|Multicolor,Objects & Tools,Time|0
clock.badge.questionmark.fill|SFClockBadgeQuestionmarkFill|Multicolor,Objects & Tools,Time|0
clock.badge.questionmark|SFClockBadgeQuestionmark|Multicolor,Objects & Tools,Time|0
clock.badge|SFClockBadge|Multicolor,Objects & Tools,Time|0
clock.badge.xmark.fill|SFClockBadgeXmarkFill|Multicolor,Objects & Tools,Time|0
clock.badge.xmark|SFClockBadgeXmark|Multicolor,Objects & Tools,Time|0
clock.circle.fill|SFClockCircleFill|Multicolor,Objects & Tools,Time|0
clock.circle|SFClockCircle|Draw,Objects & Tools,Time,Variable|0
clock.fill|SFClockFill|Objects & Tools,Time|0
clock|SFClock|Multicolor,Objects & Tools,Time|0
cloud.bolt.circle.fill|SFCloudBoltCircleFill|Multicolor,Nature,Weather|0
cloud.bolt.circle|SFCloudBoltCircle|Draw,Nature,Variable,Weather|0
cloud.bolt.fill|SFCloudBoltFill|Multicolor,Nature,Weather|0
cloud.bolt.rain.circle.fill|SFCloudBoltRainCircleFill|Multicolor,Nature,Weather|0
cloud.bolt.rain.circle|SFCloudBoltRainCircle|Draw,Nature,Variable,Weather|0
cloud.bolt.rain.fill|SFCloudBoltRainFill|Multicolor,Nature,Weather|0
cloud.bolt.rain|SFCloudBoltRain|Nature,Weather|0
cloud.bolt|SFCloudBolt|Nature,Weather|0
cloud.circle.fill|SFCloudCircleFill|Multicolor,Nature,Weather|0
cloud.circle|SFCloudCircle|Draw,Nature,Variable,Weather|0
cloud.drizzle.circle.fill|SFCloudDrizzleCircleFill|Multicolor,Nature,Weather|0
cloud.drizzle.circle|SFCloudDrizzleCircle|Draw,Nature,Variable,Weather|0
cloud.drizzle.fill|SFCloudDrizzleFill|Draw,Multicolor,Nature,Weather|0
cloud.drizzle|SFCloudDrizzle|Draw,Nature,Weather|0
cloud.fill|SFCloudFill|Multicolor,Nature,Weather|0
cloud.fog.circle.fill|SFCloudFogCircleFill|Multicolor,Nature,Weather|0
cloud.fog.circle|SFCloudFogCircle|Draw,Nature,Variable,Weather|0
cloud.fog.fill|SFCloudFogFill|Draw,Multicolor,Nature,Weather|0
cloud.fog|SFCloudFog|Draw,Nature,Weather|0
cloud.hail.circle.fill|SFCloudHailCircleFill|Multicolor,Nature,Weather|0
cloud.hail.circle|SFCloudHailCircle|Draw,Nature,Variable,Weather|0
cloud.hail.fill|SFCloudHailFill|Multicolor,Nature,Weather|0
cloud.hail|SFCloudHail|Nature,Weather|0
cloud.heavyrain.circle.fill|SFCloudHeavyrainCircleFill|Multicolor,Nature,Weather|0
cloud.heavyrain.circle|SFCloudHeavyrainCircle|Draw,Nature,Variable,Weather|0
cloud.heavyrain.fill|SFCloudHeavyrainFill|Draw,Multicolor,Nature,Weather|0
cloud.heavyrain|SFCloudHeavyrain|Draw,Nature,Weather|0
cloud.moon.bolt.circle.fill|SFCloudMoonBoltCircleFill|Multicolor,Nature,Weather|0
cloud.moon.bolt.circle|SFCloudMoonBoltCircle|Draw,Nature,Variable,Weather|0
cloud.moon.bolt.fill|SFCloudMoonBoltFill|Multicolor,Nature,Weather|0
cloud.moon.bolt|SFCloudMoonBolt|Nature,Weather|0
cloud.moon.circle.fill|SFCloudMoonCircleFill|Multicolor,Nature,Weather|0
cloud.moon.circle|SFCloudMoonCircle|Draw,Nature,Variable,Weather|0
cloud.moon.fill|SFCloudMoonFill|Multicolor,Nature,Weather|0
cloud.moon.rain.circle.fill|SFCloudMoonRainCircleFill|Multicolor,Nature,Weather|0
cloud.moon.rain.circle|SFCloudMoonRainCircle|Draw,Nature,Variable,Weather|0
cloud.moon.rain.fill|SFCloudMoonRainFill|Draw,Multicolor,Nature,Weather|0
cloud.moon.rain|SFCloudMoonRain|Draw,Nature,Weather|0
cloud.moon|SFCloudMoon|Nature,Weather|0
cloud.rain.circle.fill|SFCloudRainCircleFill|Multicolor,Nature,Weather|0
cloud.rain.circle|SFCloudRainCircle|Draw,Nature,Variable,Weather|0
cloud.rain.fill|SFCloudRainFill|Draw,Multicolor,Nature,Weather|0
cloud.rain|SFCloudRain|Draw,Nature,Weather|0
cloud.rainbow.crop.fill|SFCloudRainbowCropFill|Draw,Multicolor,Nature,Variable,Weather|0
cloud.rainbow.crop|SFCloudRainbowCrop|Draw,Multicolor,Nature,Variable,Weather|0
cloud.sleet.circle.fill|SFCloudSleetCircleFill|Multicolor,Nature,Weather|0
cloud.sleet.circle|SFCloudSleetCircle|Draw,Nature,Variable,Weather|0
cloud.sleet.fill|SFCloudSleetFill|Multicolor,Nature,Weather|0
cloud.sleet|SFCloudSleet|Nature,Weather|0
cloud.snow.circle.fill|SFCloudSnowCircleFill|Multicolor,Nature,Weather|0
cloud.snow.circle|SFCloudSnowCircle|Draw,Nature,Variable,Weather|0
cloud.snow.fill|SFCloudSnowFill|Multicolor,Nature,Weather|0
cloud.snow|SFCloudSnow|Nature,Weather|0
cloud.sun.bolt.circle.fill|SFCloudSunBoltCircleFill|Multicolor,Nature,Weather|0
cloud.sun.bolt.circle|SFCloudSunBoltCircle|Draw,Nature,Variable,Weather|0
cloud.sun.bolt.fill|SFCloudSunBoltFill|Multicolor,Nature,Weather|0
cloud.sun.bolt|SFCloudSunBolt|Nature,Weather|0
cloud.sun.circle.fill|SFCloudSunCircleFill|Multicolor,Nature,Weather|0
cloud.sun.circle|SFCloudSunCircle|Draw,Nature,Variable,Weather|0
cloud.sun.fill|SFCloudSunFill|Multicolor,Nature,Weather|0
cloud.sun.rain.circle.fill|SFCloudSunRainCircleFill|Multicolor,Nature,Weather|0
cloud.sun.rain.circle|SFCloudSunRainCircle|Draw,Nature,Variable,Weather|0
cloud.sun.rain.fill|SFCloudSunRainFill|Draw,Multicolor,Nature,Weather|0
cloud.sun.rain|SFCloudSunRain|Draw,Nature,Weather|0
cloud.sun|SFCloudSun|Nature,Weather|0
cloud|SFCloud|Nature,Weather|0
coat.circle.fill|SFCoatCircleFill|Multicolor|0
coat.circle|SFCoatCircle|Draw,Variable|0
coat.fill|SFCoatFill|Objects & Tools|0
coat|SFCoat|Objects & Tools|0
coloncurrencysign.arrow.trianglehead.counterclockwise.rotate.90|SFColoncurrencysignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
coloncurrencysign.bank.building.fill|SFColoncurrencysignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
coloncurrencysign.bank.building|SFColoncurrencysignBankBuilding|Commerce,Objects & Tools|0
coloncurrencysign.circle.fill|SFColoncurrencysignCircleFill|Commerce,Indices,Multicolor|0
coloncurrencysign.circle|SFColoncurrencysignCircle|Commerce,Draw,Indices,Variable|0
coloncurrencysign.gauge.chart.lefthalf.righthalf|SFColoncurrencysignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
coloncurrencysign.gauge.chart.leftthird.topthird.rightthird|SFColoncurrencysignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
coloncurrencysign.ring.dashed|SFColoncurrencysignRingDashed|Commerce,Home,Variable|0
coloncurrencysign.ring|SFColoncurrencysignRing|Commerce,Draw,Home|0
coloncurrencysign.square.fill|SFColoncurrencysignSquareFill|Commerce,Indices,Multicolor|0
coloncurrencysign.square|SFColoncurrencysignSquare|Commerce,Draw,Indices|0
coloncurrencysign|SFColoncurrencysign|Commerce,Indices|0
comb.fill|SFCombFill|Objects & Tools|0
comb|SFComb|Objects & Tools|0
command.circle.fill|SFCommandCircleFill|Keyboard,Multicolor|0
command.circle|SFCommandCircle|Draw,Keyboard,Variable|0
command.square.fill|SFCommandSquareFill|Keyboard,Multicolor|0
command.square|SFCommandSquare|Draw,Keyboard|0
command|SFCommand|Draw,Keyboard|0
compass.drawing|SFCompassDrawing|Math,Objects & Tools|0
computermouse.fill|SFComputermouseFill|Devices|0
computermouse|SFComputermouse|Devices|0
cone.fill|SFConeFill||0
cone|SFCone||0
contact.sensor.fill|SFContactSensorFill|Home|0
contact.sensor|SFContactSensor|Home|0
contextualmenu.and.pointer.arrow|SFContextualmenuAndPointerArrow|Accessibility,Draw|0
control|SFControl|Keyboard|0
convertible.side.air.circulate.fill|SFConvertibleSideAirCirculateFill|Automotive,Multicolor|0
convertible.side.air.circulate|SFConvertibleSideAirCirculate|Automotive|0
convertible.side.air.fresh.fill|SFConvertibleSideAirFreshFill|Automotive|0
convertible.side.air.fresh|SFConvertibleSideAirFresh|Automotive|0
convertible.side.and.exclamationmark.fill|SFConvertibleSideAndExclamationmarkFill|Automotive|0
convertible.side.and.exclamationmark|SFConvertibleSideAndExclamationmark|Automotive|0
convertible.side.arrow.left.and.right.fill|SFConvertibleSideArrowLeftAndRightFill|Automotive,Draw|0
convertible.side.arrow.left.and.right|SFConvertibleSideArrowLeftAndRight|Automotive,Draw|0
convertible.side.arrow.trianglehead.backward.fill|SFConvertibleSideArrowTriangleheadBackwardFill|Automotive,Draw|0
convertible.side.arrow.trianglehead.backward|SFConvertibleSideArrowTriangleheadBackward|Automotive,Draw|0
convertible.side.arrow.trianglehead.forward.and.backward.fill|SFConvertibleSideArrowTriangleheadForwardAndBackwardFill|Automotive,Draw|0
convertible.side.arrow.trianglehead.forward.and.backward|SFConvertibleSideArrowTriangleheadForwardAndBackward|Automotive,Draw|0
convertible.side.arrow.trianglehead.forward.fill|SFConvertibleSideArrowTriangleheadForwardFill|Automotive,Draw|0
convertible.side.arrow.trianglehead.forward|SFConvertibleSideArrowTriangleheadForward|Automotive,Draw|0
convertible.side.arrowtriangle.down.fill|SFConvertibleSideArrowtriangleDownFill|Automotive|0
convertible.side.arrowtriangle.down|SFConvertibleSideArrowtriangleDown|Automotive|0
convertible.side.arrowtriangle.up.arrowtriangle.down.fill|SFConvertibleSideArrowtriangleUpArrowtriangleDownFill|Automotive|0
convertible.side.arrowtriangle.up.arrowtriangle.down|SFConvertibleSideArrowtriangleUpArrowtriangleDown|Automotive|0
convertible.side.arrowtriangle.up.fill|SFConvertibleSideArrowtriangleUpFill|Automotive|0
convertible.side.arrowtriangle.up|SFConvertibleSideArrowtriangleUp|Automotive|0
convertible.side.fill|SFConvertibleSideFill|Automotive|0
convertible.side.front.open.crop.fill|SFConvertibleSideFrontOpenCropFill|Automotive|0
convertible.side.front.open.crop|SFConvertibleSideFrontOpenCrop|Automotive|0
convertible.side.front.open.fill|SFConvertibleSideFrontOpenFill|Automotive|0
convertible.side.front.open|SFConvertibleSideFrontOpen|Automotive|0
convertible.side.hill.descent.control.fill|SFConvertibleSideHillDescentControlFill|Automotive,Draw|0
convertible.side.hill.descent.control|SFConvertibleSideHillDescentControl|Automotive,Draw|0
convertible.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle.fill|SFConvertibleSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangleFill|Automotive,Draw|0
convertible.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle|SFConvertibleSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangle|Automotive,Draw|0
convertible.side.hill.down.fill|SFConvertibleSideHillDownFill|Automotive,Draw|0
convertible.side.hill.down|SFConvertibleSideHillDown|Automotive,Draw|0
convertible.side.hill.up.fill|SFConvertibleSideHillUpFill|Automotive,Draw|0
convertible.side.hill.up|SFConvertibleSideHillUp|Automotive,Draw|0
convertible.side.lock.fill|SFConvertibleSideLockFill|Automotive|0
convertible.side.lock.open.fill|SFConvertibleSideLockOpenFill|Automotive|0
convertible.side.lock.open|SFConvertibleSideLockOpen|Automotive|0
convertible.side.lock|SFConvertibleSideLock|Automotive|0
convertible.side|SFConvertibleSide|Automotive|0
cooktop.fill|SFCooktopFill|Home,Objects & Tools|0
cooktop|SFCooktop|Home,Objects & Tools|0
cpu.fill|SFCpuFill|Objects & Tools|0
cpu|SFCpu|Objects & Tools|0
creditcard.and.numbers|SFCreditcardAndNumbers|Commerce|0
creditcard.arrow.trianglehead.2.clockwise.rotate.90|SFCreditcardArrowTrianglehead2ClockwiseRotate90|Commerce,Draw,Objects & Tools|0
creditcard.circle.fill|SFCreditcardCircleFill|Commerce,Multicolor,Objects & Tools|0
creditcard.circle|SFCreditcardCircle|Commerce,Draw,Objects & Tools,Variable|0
creditcard.fill|SFCreditcardFill|Commerce,Objects & Tools|0
creditcard.rewards.fill|SFCreditcardRewardsFill|Commerce|0
creditcard.rewards|SFCreditcardRewards|Commerce|0
creditcard|SFCreditcard|Commerce,Objects & Tools|0
creditcard.trianglebadge.exclamationmark.fill|SFCreditcardTrianglebadgeExclamationmarkFill|Commerce,Multicolor|0
creditcard.trianglebadge.exclamationmark|SFCreditcardTrianglebadgeExclamationmark|Commerce,Multicolor|0
creditcard.viewfinder|SFCreditcardViewfinder|Objects & Tools|0
cricket.ball.circle.fill|SFCricketBallCircleFill|Fitness,Multicolor,Objects & Tools|0
cricket.ball.circle|SFCricketBallCircle|Draw,Fitness,Objects & Tools,Variable|0
cricket.ball.fill|SFCricketBallFill|Fitness,Objects & Tools|0
cricket.ball|SFCricketBall|Fitness,Objects & Tools|0
crop.rotate|SFCropRotate|Draw,Editing,Objects & Tools|0
crop|SFCrop|Draw,Editing,Objects & Tools|0
cross.case.circle.fill|SFCrossCaseCircleFill|Health,Multicolor,Objects & Tools|0
cross.case.circle|SFCrossCaseCircle|Draw,Health,Objects & Tools,Variable|0
cross.case.fill|SFCrossCaseFill|Health,Objects & Tools|0
cross.case|SFCrossCase|Health,Objects & Tools|0
cross.circle.fill|SFCrossCircleFill|Health,Multicolor|0
cross.circle|SFCrossCircle|Draw,Health,Multicolor,Variable|0
cross.fill|SFCrossFill|Health,Multicolor|0
cross|SFCross|Health,Multicolor|0
cross.vial.fill|SFCrossVialFill|Health,Multicolor,Objects & Tools|0
cross.vial|SFCrossVial|Health,Multicolor,Objects & Tools|0
crown.fill|SFCrownFill|Objects & Tools|0
crown|SFCrown|Objects & Tools|0
cruzeirosign.arrow.trianglehead.counterclockwise.rotate.90|SFCruzeirosignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
cruzeirosign.bank.building.fill|SFCruzeirosignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
cruzeirosign.bank.building|SFCruzeirosignBankBuilding|Commerce,Objects & Tools|0
cruzeirosign.circle.fill|SFCruzeirosignCircleFill|Commerce,Indices,Multicolor|0
cruzeirosign.circle|SFCruzeirosignCircle|Commerce,Draw,Indices,Variable|0
cruzeirosign.gauge.chart.lefthalf.righthalf|SFCruzeirosignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
cruzeirosign.gauge.chart.leftthird.topthird.rightthird|SFCruzeirosignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
cruzeirosign.ring.dashed|SFCruzeirosignRingDashed|Commerce,Home,Variable|0
cruzeirosign.ring|SFCruzeirosignRing|Commerce,Draw,Home|0
cruzeirosign.square.fill|SFCruzeirosignSquareFill|Commerce,Indices,Multicolor|0
cruzeirosign.square|SFCruzeirosignSquare|Commerce,Draw,Indices|0
cruzeirosign|SFCruzeirosign|Commerce,Indices|0
cube.circle.fill|SFCubeCircleFill|Multicolor,Objects & Tools|0
cube.circle|SFCubeCircle|Draw,Objects & Tools,Variable|0
cube.fill|SFCubeFill|Objects & Tools|0
cube|SFCube|Objects & Tools|0
cube.transparent.fill|SFCubeTransparentFill||0
cube.transparent|SFCubeTransparent||0
cup.and.heat.waves.fill|SFCupAndHeatWavesFill|Objects & Tools|0
cup.and.heat.waves|SFCupAndHeatWaves|Objects & Tools|0
cup.and.saucer.fill|SFCupAndSaucerFill|Objects & Tools|0
cup.and.saucer|SFCupAndSaucer|Objects & Tools|0
curlybraces.square.fill|SFCurlybracesSquareFill|Multicolor|0
curlybraces.square|SFCurlybracesSquare|Draw|0
curlybraces|SFCurlybraces||0
curtains.closed|SFCurtainsClosed|Home|0
curtains.open|SFCurtainsOpen|Home|0
cylinder.fill|SFCylinderFill||0
cylinder.split.1x2.fill|SFCylinderSplit1x2Fill||0
cylinder.split.1x2|SFCylinderSplit1x2||0
cylinder|SFCylinder||0
d.circle.fill|SFDCircleFill|Indices,Multicolor|0
d.circle|SFDCircle|Draw,Indices,Variable|0
d.square.fill|SFDSquareFill|Indices,Multicolor|0
d.square|SFDSquare|Draw,Indices|0
danishkronesign.arrow.trianglehead.counterclockwise.rotate.90|SFDanishkronesignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
danishkronesign.bank.building.fill|SFDanishkronesignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
danishkronesign.bank.building|SFDanishkronesignBankBuilding|Commerce,Objects & Tools|0
danishkronesign.circle.fill|SFDanishkronesignCircleFill|Commerce,Indices,Multicolor|0
danishkronesign.circle|SFDanishkronesignCircle|Commerce,Draw,Indices,Variable|0
danishkronesign.gauge.chart.lefthalf.righthalf|SFDanishkronesignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
danishkronesign.gauge.chart.leftthird.topthird.rightthird|SFDanishkronesignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
danishkronesign.ring.dashed|SFDanishkronesignRingDashed|Commerce,Home,Variable|0
danishkronesign.ring|SFDanishkronesignRing|Commerce,Draw,Home|0
danishkronesign.square.fill|SFDanishkronesignSquareFill|Commerce,Indices,Multicolor|0
danishkronesign.square|SFDanishkronesignSquare|Commerce,Draw,Indices|0
danishkronesign|SFDanishkronesign|Commerce,Indices|0
decrease.indent|SFDecreaseIndent|Draw,Multicolor,Text Formatting|0
decrease.quotelevel|SFDecreaseQuotelevel|Draw,Multicolor,Text Formatting|0
degreesign.celsius|SFDegreesignCelsius|Weather|0
degreesign.fahrenheit|SFDegreesignFahrenheit|Weather|0
dehumidifier.fill|SFDehumidifierFill|Home,Objects & Tools|0
dehumidifier|SFDehumidifier|Home,Objects & Tools|0
delete.backward.fill|SFDeleteBackwardFill|Keyboard,Multicolor|0
delete.backward|SFDeleteBackward|Draw,Keyboard,Multicolor|0
delete.forward.fill|SFDeleteForwardFill|Keyboard,Multicolor|0
delete.forward|SFDeleteForward|Draw,Keyboard,Multicolor|0
delete.left.fill|SFDeleteLeftFill|Keyboard,Multicolor|0
delete.left|SFDeleteLeft|Draw,Keyboard,Multicolor|0
delete.right.fill|SFDeleteRightFill|Keyboard,Multicolor|0
delete.right|SFDeleteRight|Draw,Keyboard,Multicolor|0
deskclock.fill|SFDeskclockFill|Objects & Tools,Time|0
deskclock|SFDeskclock|Objects & Tools,Time|0
desktopcomputer.and.arrow.down|SFDesktopcomputerAndArrowDown|Devices,Draw|0
desktopcomputer.and.macbook|SFDesktopcomputerAndMacbook|Devices|0
desktopcomputer.badge.checkmark|SFDesktopcomputerBadgeCheckmark|Devices,Multicolor|0
desktopcomputer.badge.shield.checkmark|SFDesktopcomputerBadgeShieldCheckmark|Devices,Multicolor|0
desktopcomputer|SFDesktopcomputer|Devices|0
desktopcomputer.trianglebadge.exclamationmark|SFDesktopcomputerTrianglebadgeExclamationmark|Devices,Multicolor|0
deskview.fill|SFDeskviewFill|Communication,Draw|1
deskview|SFDeskview|Communication,Draw|1
dial.high.fill|SFDialHighFill|Editing,Objects & Tools|0
dial.high|SFDialHigh|Editing,Objects & Tools|0
dial.low.fill|SFDialLowFill|Editing,Objects & Tools|0
dial.low|SFDialLow|Editing,Objects & Tools|0
dial.medium.fill|SFDialMediumFill|Editing,Objects & Tools|0
dial.medium|SFDialMedium|Editing,Objects & Tools|0
diamond.bottomhalf.filled|SFDiamondBottomhalfFilled||0
diamond.circle.fill|SFDiamondCircleFill|Multicolor|0
diamond.circle|SFDiamondCircle|Draw,Variable|0
diamond.fill|SFDiamondFill|Shapes|0
diamond.lefthalf.filled|SFDiamondLefthalfFilled||0
diamond.righthalf.filled|SFDiamondRighthalfFilled||0
diamond|SFDiamond|Shapes|0
diamond.tophalf.filled|SFDiamondTophalfFilled||0
dice.fill|SFDiceFill|Objects & Tools|0
dice|SFDice|Objects & Tools|0
die.face.1.fill|SFDieFace1Fill|Multicolor,Objects & Tools|0
die.face.1|SFDieFace1|Objects & Tools|0
die.face.2.fill|SFDieFace2Fill|Multicolor,Objects & Tools|0
die.face.2|SFDieFace2|Objects & Tools|0
die.face.3.fill|SFDieFace3Fill|Multicolor,Objects & Tools|0
die.face.3|SFDieFace3|Objects & Tools|0
die.face.4.fill|SFDieFace4Fill|Multicolor,Objects & Tools|0
die.face.4|SFDieFace4|Objects & Tools|0
die.face.5.fill|SFDieFace5Fill|Multicolor,Objects & Tools|0
die.face.5|SFDieFace5|Objects & Tools|0
die.face.6.fill|SFDieFace6Fill|Multicolor,Objects & Tools|0
die.face.6|SFDieFace6|Objects & Tools|0
digitalcrown.arrow.clockwise.fill|SFDigitalcrownArrowClockwiseFill|Accessibility,Devices,Draw|1
digitalcrown.arrow.clockwise|SFDigitalcrownArrowClockwise|Accessibility,Devices,Draw|1
digitalcrown.arrow.counterclockwise.fill|SFDigitalcrownArrowCounterclockwiseFill|Accessibility,Devices,Draw|1
digitalcrown.arrow.counterclockwise|SFDigitalcrownArrowCounterclockwise|Accessibility,Devices,Draw|1
digitalcrown.fill|SFDigitalcrownFill|Accessibility,Devices|0
digitalcrown.horizontal.arrow.clockwise.fill|SFDigitalcrownHorizontalArrowClockwiseFill|Devices,Draw|1
digitalcrown.horizontal.arrow.clockwise|SFDigitalcrownHorizontalArrowClockwise|Devices,Draw|1
digitalcrown.horizontal.arrow.counterclockwise.fill|SFDigitalcrownHorizontalArrowCounterclockwiseFill|Devices,Draw|1
digitalcrown.horizontal.arrow.counterclockwise|SFDigitalcrownHorizontalArrowCounterclockwise|Devices,Draw|1
digitalcrown.horizontal.fill|SFDigitalcrownHorizontalFill|Accessibility,Devices|0
digitalcrown.horizontal.press.fill|SFDigitalcrownHorizontalPressFill|Devices,Draw|1
digitalcrown.horizontal.press|SFDigitalcrownHorizontalPress|Devices,Draw|1
digitalcrown.horizontal|SFDigitalcrownHorizontal|Accessibility,Devices|0
digitalcrown.press.fill|SFDigitalcrownPressFill|Accessibility,Devices,Draw|1
digitalcrown.press|SFDigitalcrownPress|Accessibility,Devices,Draw|1
digitalcrown|SFDigitalcrown|Accessibility,Devices|0
directcurrent|SFDirectcurrent|Draw|0
dishwasher.circle.fill|SFDishwasherCircleFill|Home,Multicolor,Objects & Tools|0
dishwasher.circle|SFDishwasherCircle|Draw,Home,Objects & Tools,Variable|0
dishwasher.fill|SFDishwasherFill|Home,Objects & Tools|0
dishwasher|SFDishwasher|Home,Objects & Tools|0
display.2|SFDisplay2|Devices|0
display.and.arrow.down|SFDisplayAndArrowDown|Devices,Draw|0
display.and.screwdriver|SFDisplayAndScrewdriver|Devices|0
display|SFDisplay|Devices|0
display.trianglebadge.exclamationmark|SFDisplayTrianglebadgeExclamationmark|Devices,Multicolor|0
distribute.horizontal.center.fill|SFDistributeHorizontalCenterFill|Editing|0
distribute.horizontal.center|SFDistributeHorizontalCenter|Editing|0
distribute.horizontal.fill|SFDistributeHorizontalFill|Draw,Editing|0
distribute.horizontal.left.fill|SFDistributeHorizontalLeftFill|Draw,Editing|0
distribute.horizontal.left|SFDistributeHorizontalLeft|Draw,Editing|0
distribute.horizontal.right.fill|SFDistributeHorizontalRightFill|Draw,Editing|0
distribute.horizontal.right|SFDistributeHorizontalRight|Draw,Editing|0
distribute.horizontal|SFDistributeHorizontal|Draw,Editing|0
distribute.vertical.bottom.fill|SFDistributeVerticalBottomFill|Draw,Editing|0
distribute.vertical.bottom|SFDistributeVerticalBottom|Draw,Editing|0
distribute.vertical.center.fill|SFDistributeVerticalCenterFill|Editing|0
distribute.vertical.center|SFDistributeVerticalCenter|Editing|0
distribute.vertical.fill|SFDistributeVerticalFill|Draw,Editing|0
distribute.vertical|SFDistributeVertical|Draw,Editing|0
distribute.vertical.top.fill|SFDistributeVerticalTopFill|Draw,Editing|0
distribute.vertical.top|SFDistributeVerticalTop|Draw,Editing|0
divide.circle.fill|SFDivideCircleFill|Math,Multicolor|0
divide.circle|SFDivideCircle|Draw,Math,Variable|0
divide.square.fill|SFDivideSquareFill|Math,Multicolor|0
divide.square|SFDivideSquare|Draw,Math|0
divide|SFDivide|Math|0
dock.arrow.down.rectangle|SFDockArrowDownRectangle|Draw|0
dock.arrow.up.rectangle|SFDockArrowUpRectangle|Draw|0
dock.rectangle|SFDockRectangle||0
document.badge.arrow.up.fill|SFDocumentBadgeArrowUpFill|Multicolor,Objects & Tools|0
document.badge.arrow.up|SFDocumentBadgeArrowUp|Multicolor,Objects & Tools|0
document.badge.clock.fill|SFDocumentBadgeClockFill|Multicolor,Objects & Tools|0
document.badge.clock|SFDocumentBadgeClock|Multicolor,Objects & Tools|0
document.badge.ellipsis.fill|SFDocumentBadgeEllipsisFill|Multicolor,Objects & Tools|0
document.badge.ellipsis|SFDocumentBadgeEllipsis|Multicolor,Objects & Tools|0
document.badge.gearshape.fill|SFDocumentBadgeGearshapeFill|Objects & Tools|0
document.badge.gearshape|SFDocumentBadgeGearshape|Objects & Tools|0
document.badge.plus.fill|SFDocumentBadgePlusFill|Multicolor,Objects & Tools|0
document.badge.plus|SFDocumentBadgePlus|Multicolor,Objects & Tools|0
document.circle.fill|SFDocumentCircleFill|Multicolor,Objects & Tools|0
document.circle|SFDocumentCircle|Draw,Objects & Tools,Variable|0
document.fill|SFDocumentFill|Objects & Tools|0
document.on.clipboard.fill|SFDocumentOnClipboardFill|Objects & Tools|0
document.on.clipboard|SFDocumentOnClipboard|Objects & Tools|0
document.on.document.fill|SFDocumentOnDocumentFill|Objects & Tools|0
document.on.document|SFDocumentOnDocument|Objects & Tools|0
document.on.trash.fill|SFDocumentOnTrashFill|Objects & Tools|0
document.on.trash|SFDocumentOnTrash|Objects & Tools|0
document|SFDocument|Objects & Tools|0
document.viewfinder.fill|SFDocumentViewfinderFill|Objects & Tools|0
document.viewfinder|SFDocumentViewfinder|Objects & Tools|0
dog.circle.fill|SFDogCircleFill|Multicolor,Nature|0
dog.circle|SFDogCircle|Draw,Nature,Variable|0
dog.fill|SFDogFill|Nature|0
dog|SFDog|Nature|0
dollarsign.arrow.trianglehead.counterclockwise.rotate.90|SFDollarsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
dollarsign.bank.building.fill|SFDollarsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
dollarsign.bank.building|SFDollarsignBankBuilding|Commerce,Objects & Tools|0
dollarsign.circle.fill|SFDollarsignCircleFill|Commerce,Indices,Multicolor|0
dollarsign.circle|SFDollarsignCircle|Commerce,Draw,Indices,Variable|0
dollarsign.gauge.chart.lefthalf.righthalf|SFDollarsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
dollarsign.gauge.chart.leftthird.topthird.rightthird|SFDollarsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
dollarsign.ring.dashed|SFDollarsignRingDashed|Commerce,Home,Variable|0
dollarsign.ring|SFDollarsignRing|Commerce,Draw,Home|0
dollarsign.square.fill|SFDollarsignSquareFill|Commerce,Indices,Multicolor|0
dollarsign.square|SFDollarsignSquare|Commerce,Draw,Indices|0
dollarsign|SFDollarsign|Commerce|0
dongsign.arrow.trianglehead.counterclockwise.rotate.90|SFDongsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
dongsign.bank.building.fill|SFDongsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
dongsign.bank.building|SFDongsignBankBuilding|Commerce,Objects & Tools|0
dongsign.circle.fill|SFDongsignCircleFill|Commerce,Indices,Multicolor|0
dongsign.circle|SFDongsignCircle|Commerce,Draw,Indices,Variable|0
dongsign.gauge.chart.lefthalf.righthalf|SFDongsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
dongsign.gauge.chart.leftthird.topthird.rightthird|SFDongsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
dongsign.ring.dashed|SFDongsignRingDashed|Commerce,Home,Variable|0
dongsign.ring|SFDongsignRing|Commerce,Draw,Home|0
dongsign.square.fill|SFDongsignSquareFill|Commerce,Indices,Multicolor|0
dongsign.square|SFDongsignSquare|Commerce,Draw,Indices|0
dongsign|SFDongsign|Commerce,Indices|0
door.french.closed|SFDoorFrenchClosed|Home,Objects & Tools|0
door.french.open|SFDoorFrenchOpen|Home,Objects & Tools|0
door.garage.closed|SFDoorGarageClosed|Home,Objects & Tools|0
door.garage.closed.trianglebadge.exclamationmark|SFDoorGarageClosedTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools|0
door.garage.double.bay.closed|SFDoorGarageDoubleBayClosed|Home,Objects & Tools|0
door.garage.double.bay.closed.trianglebadge.exclamationmark|SFDoorGarageDoubleBayClosedTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools|0
door.garage.double.bay.open|SFDoorGarageDoubleBayOpen|Home,Objects & Tools|0
door.garage.double.bay.open.trianglebadge.exclamationmark|SFDoorGarageDoubleBayOpenTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools|0
door.garage.open|SFDoorGarageOpen|Home,Objects & Tools|0
door.garage.open.trianglebadge.exclamationmark|SFDoorGarageOpenTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools|0
door.left.hand.closed|SFDoorLeftHandClosed|Home,Objects & Tools|0
door.left.hand.open|SFDoorLeftHandOpen|Home,Objects & Tools|0
door.right.hand.closed|SFDoorRightHandClosed|Home,Objects & Tools|0
door.right.hand.open|SFDoorRightHandOpen|Home,Objects & Tools|0
door.sliding.left.hand.closed|SFDoorSlidingLeftHandClosed|Home,Objects & Tools|0
door.sliding.left.hand.open|SFDoorSlidingLeftHandOpen|Home,Objects & Tools|0
door.sliding.right.hand.closed|SFDoorSlidingRightHandClosed|Home,Objects & Tools|0
door.sliding.right.hand.open|SFDoorSlidingRightHandOpen|Home,Objects & Tools|0
dot.arrowtriangles.up.right.down.left.circle|SFDotArrowtrianglesUpRightDownLeftCircle|Accessibility,Draw,Variable|0
dot.car.top.radiowaves.2.rear.left.rear.rear.right.fill|SFDotCarTopRadiowaves2RearLeftRearRearRightFill|Automotive,Variable|0
dot.car.top.radiowaves.2.rear.left.rear.rear.right|SFDotCarTopRadiowaves2RearLeftRearRearRight|Automotive,Variable|0
dot.circle.and.hand.point.up.left.fill|SFDotCircleAndHandPointUpLeftFill|Accessibility|0
dot.circle.and.pointer.arrow|SFDotCircleAndPointerArrow|Accessibility|0
dot.circle.viewfinder|SFDotCircleViewfinder||0
dot.crosshair|SFDotCrosshair|Draw|0
dot.radiowaves.forward|SFDotRadiowavesForward|Connectivity,Draw,Variable|0
dot.radiowaves.left.and.right|SFDotRadiowavesLeftAndRight|Connectivity,Draw,Variable|0
dot.radiowaves.right|SFDotRadiowavesRight|Connectivity,Draw,Variable|0
dot.radiowaves.up.forward|SFDotRadiowavesUpForward|Connectivity,Draw,Variable|0
dot.scope.display|SFDotScopeDisplay|Devices|0
dot.scope.laptopcomputer|SFDotScopeLaptopcomputer|Devices|0
dot.scope|SFDotScope|Camera & Photos,Draw|0
dot.square.fill|SFDotSquareFill|Multicolor|0
dot.square|SFDotSquare|Draw|0
dot.squareshape.fill|SFDotSquareshapeFill|Multicolor|0
dot.squareshape.split.2x2|SFDotSquareshapeSplit2x2||0
dot.squareshape|SFDotSquareshape||0
dot.viewfinder|SFDotViewfinder||0
dots.and.line.vertical.and.pointer.arrow.rectangle|SFDotsAndLineVerticalAndPointerArrowRectangle||0
dpad.down.filled|SFDpadDownFilled|Gaming|0
dpad.fill|SFDpadFill|Gaming|0
dpad.left.filled|SFDpadLeftFilled|Gaming|0
dpad.right.filled|SFDpadRightFilled|Gaming|0
dpad|SFDpad|Gaming|0
dpad.up.filled|SFDpadUpFilled|Gaming|0
drone.fill|SFDroneFill|Objects & Tools|0
drone|SFDrone|Objects & Tools|0
drop.circle.fill|SFDropCircleFill|Multicolor,Nature|0
drop.circle|SFDropCircle|Draw,Nature,Variable|0
drop.degreesign.fill|SFDropDegreesignFill|Nature|0
drop.degreesign.slash.fill|SFDropDegreesignSlashFill|Draw,Nature|0
drop.degreesign.slash|SFDropDegreesignSlash|Draw,Nature|0
drop.degreesign|SFDropDegreesign|Nature|0
drop.fill|SFDropFill|Nature|0
drop.halffull|SFDropHalffull|Camera & Photos|0
drop.keypad.rectangle.fill|SFDropKeypadRectangleFill|Home,Objects & Tools|0
drop.keypad.rectangle|SFDropKeypadRectangle|Home,Objects & Tools|0
drop|SFDrop|Nature|0
drop.transmission|SFDropTransmission|Automotive|0
drop.triangle.fill|SFDropTriangleFill|Multicolor,Nature|0
drop.triangle|SFDropTriangle|Nature|0
dryer.circle.fill|SFDryerCircleFill|Home,Multicolor,Objects & Tools|0
dryer.circle|SFDryerCircle|Draw,Home,Objects & Tools,Variable|0
dryer.fill|SFDryerFill|Home,Objects & Tools|0
dryer|SFDryer|Home,Objects & Tools|0
duffle.bag.fill|SFDuffleBagFill|Fitness,Objects & Tools|0
duffle.bag|SFDuffleBag|Fitness,Objects & Tools|0
dumbbell.fill|SFDumbbellFill|Fitness,Objects & Tools|0
dumbbell|SFDumbbell|Fitness,Objects & Tools|0
e.circle.fill|SFECircleFill|Indices,Multicolor|0
e.circle|SFECircle|Draw,Indices,Variable|0
e.square.fill|SFESquareFill|Indices,Multicolor|0
e.square|SFESquare|Draw,Indices|0
ear.badge.checkmark|SFEarBadgeCheckmark|Accessibility,Health,Human,Multicolor|0
ear.badge.waveform|SFEarBadgeWaveform|Accessibility,Draw,Health,Human,Variable|0
ear.fill|SFEarFill|Accessibility,Health,Human,Multicolor|0
ear|SFEar|Accessibility,Health,Human,Multicolor|0
ear.trianglebadge.exclamationmark|SFEarTrianglebadgeExclamationmark|Accessibility,Health,Human,Multicolor|0
earbud.left|SFEarbudLeft|Devices,Objects & Tools|0
earbud.right|SFEarbudRight|Devices,Objects & Tools|0
earbuds.bone.conduction.left|SFEarbudsBoneConductionLeft|Devices,Objects & Tools|0
earbuds.bone.conduction.right|SFEarbudsBoneConductionRight|Devices,Objects & Tools|0
earbuds.bone.conduction|SFEarbudsBoneConduction|Devices,Objects & Tools|0
earbuds.case.fill|SFEarbudsCaseFill|Devices,Objects & Tools|0
earbuds.case|SFEarbudsCase|Devices,Objects & Tools|0
earbuds.in.ear.left|SFEarbudsInEarLeft|Devices,Objects & Tools|0
earbuds.in.ear.right|SFEarbudsInEarRight|Devices,Objects & Tools|0
earbuds.in.ear|SFEarbudsInEar|Devices,Objects & Tools|0
earbuds.stemless.left|SFEarbudsStemlessLeft|Devices,Objects & Tools|0
earbuds.stemless.right|SFEarbudsStemlessRight|Devices,Objects & Tools|0
earbuds.stemless|SFEarbudsStemless|Devices,Objects & Tools|0
earbuds|SFEarbuds|Devices,Objects & Tools|0
earpods|SFEarpods|Devices|1
eject.circle.fill|SFEjectCircleFill|Keyboard,Multicolor|0
eject.circle|SFEjectCircle|Draw,Keyboard,Variable|0
eject.fill|SFEjectFill|Keyboard|0
eject|SFEject|Keyboard|0
electronic.toll.collection.rectangle.fill|SFElectronicTollCollectionRectangleFill|Automotive,Multicolor|0
electronic.toll.collection.rectangle.slash.fill|SFElectronicTollCollectionRectangleSlashFill|Automotive|0
electronic.toll.collection.rectangle.slash|SFElectronicTollCollectionRectangleSlash|Automotive|0
electronic.toll.collection.rectangle|SFElectronicTollCollectionRectangle|Automotive|0
electronic.toll.collection.rectangle.trianglebadge.exclamationmark.fill|SFElectronicTollCollectionRectangleTrianglebadgeExclamationmarkFill|Automotive,Multicolor|0
electronic.toll.collection.rectangle.trianglebadge.exclamationmark|SFElectronicTollCollectionRectangleTrianglebadgeExclamationmark|Automotive,Multicolor|0
electronic.toll.collection|SFElectronicTollCollection|Automotive|0
ellipsis.bubble.fill|SFEllipsisBubbleFill|Accessibility,Communication,Multicolor,Variable|0
ellipsis.bubble|SFEllipsisBubble|Accessibility,Communication,Variable|0
ellipsis.calendar|SFEllipsisCalendar|Objects & Tools,Variable|0
ellipsis.circle.badge.fill|SFEllipsisCircleBadgeFill|Multicolor,Variable|0
ellipsis.circle.badge|SFEllipsisCircleBadge|Multicolor,Variable|0
ellipsis.circle.fill|SFEllipsisCircleFill|Multicolor,Variable|0
ellipsis.circle|SFEllipsisCircle|Draw,Variable|0
ellipsis.curlybraces|SFEllipsisCurlybraces|Variable|0
ellipsis.message.fill|SFEllipsisMessageFill|Communication,Multicolor,Variable|1
ellipsis.message|SFEllipsisMessage|Communication,Variable|1
ellipsis.rectangle.fill|SFEllipsisRectangleFill|Multicolor,Variable|0
ellipsis.rectangle|SFEllipsisRectangle|Variable|0
ellipsis|SFEllipsis|Variable|0
ellipsis.vertical.bubble.fill|SFEllipsisVerticalBubbleFill|Communication,Multicolor,Variable|0
ellipsis.vertical.bubble|SFEllipsisVerticalBubble|Communication,Variable|0
ellipsis.viewfinder|SFEllipsisViewfinder|Variable|0
engine.combustion.badge.exclamationmark.fill|SFEngineCombustionBadgeExclamationmarkFill|Automotive,Multicolor|0
engine.combustion.badge.exclamationmark|SFEngineCombustionBadgeExclamationmark|Automotive,Multicolor|0
engine.combustion.fill|SFEngineCombustionFill|Automotive|0
engine.combustion|SFEngineCombustion|Automotive|0
engine.emission.and.drop.2.water.wave.below|SFEngineEmissionAndDrop2WaterWaveBelow|Automotive,Draw|0
engine.emission.and.exclamationmark|SFEngineEmissionAndExclamationmark|Automotive,Draw|0
engine.emission.and.filter|SFEngineEmissionAndFilter|Automotive,Draw|0
entry.lever.keypad.fill|SFEntryLeverKeypadFill|Home,Objects & Tools|0
entry.lever.keypad|SFEntryLeverKeypad|Home,Objects & Tools|0
entry.lever.keypad.trianglebadge.exclamationmark.fill|SFEntryLeverKeypadTrianglebadgeExclamationmarkFill|Home,Multicolor,Objects & Tools|0
entry.lever.keypad.trianglebadge.exclamationmark|SFEntryLeverKeypadTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools|0
envelope.and.arrow.3.down.fill|SFEnvelopeAndArrow3DownFill|Draw|0
envelope.and.arrow.3.down|SFEnvelopeAndArrow3Down|Draw|0
envelope.and.arrow.trianglehead.branch.fill|SFEnvelopeAndArrowTriangleheadBranchFill|Communication|0
envelope.and.arrow.trianglehead.branch|SFEnvelopeAndArrowTriangleheadBranch|Communication,Draw|0
envelope.and.hand.raised.fill|SFEnvelopeAndHandRaisedFill|Communication,Privacy & Security|0
envelope.and.hand.raised|SFEnvelopeAndHandRaised|Communication,Privacy & Security|0
envelope.badge.fill|SFEnvelopeBadgeFill|Communication|0
envelope.badge.minus.fill|SFEnvelopeBadgeMinusFill|Communication,Multicolor|0
envelope.badge.minus|SFEnvelopeBadgeMinus|Communication,Multicolor|0
envelope.badge.person.crop.fill|SFEnvelopeBadgePersonCropFill|Communication,Multicolor|0
envelope.badge.person.crop|SFEnvelopeBadgePersonCrop|Communication,Multicolor|0
envelope.badge.plus.fill|SFEnvelopeBadgePlusFill|Communication,Multicolor|0
envelope.badge.plus|SFEnvelopeBadgePlus|Communication,Multicolor|0
envelope.badge.shield.half.filled.fill|SFEnvelopeBadgeShieldHalfFilledFill|Communication,Privacy & Security|0
envelope.badge.shield.half.filled|SFEnvelopeBadgeShieldHalfFilled|Communication,Privacy & Security|0
envelope.badge|SFEnvelopeBadge|Communication,Multicolor|0
envelope.circle.fill|SFEnvelopeCircleFill|Communication,Multicolor|0
envelope.circle|SFEnvelopeCircle|Communication,Draw,Variable|0
envelope.fill|SFEnvelopeFill|Communication|0
envelope.front.fill|SFEnvelopeFrontFill|Communication,Draw,Multicolor|0
envelope.front|SFEnvelopeFront|Communication,Draw|0
envelope.open.badge.clock.fill|SFEnvelopeOpenBadgeClockFill|Communication,Multicolor|0
envelope.open.badge.clock|SFEnvelopeOpenBadgeClock|Communication,Multicolor|0
envelope.open.fill|SFEnvelopeOpenFill|Communication|0
envelope.open|SFEnvelopeOpen|Communication|0
envelope.stack.fill|SFEnvelopeStackFill|Communication|0
envelope.stack|SFEnvelopeStack|Communication|0
envelope|SFEnvelope|Communication|0
environments.circle.fill|SFEnvironmentsCircleFill|Multicolor,Nature|1
environments.circle|SFEnvironmentsCircle|Draw,Nature,Variable|1
environments.fill|SFEnvironmentsFill|Nature|1
environments.slash.circle.fill|SFEnvironmentsSlashCircleFill|Multicolor,Nature|1
environments.slash.circle|SFEnvironmentsSlashCircle|Draw,Nature,Variable|1
environments.slash.fill|SFEnvironmentsSlashFill|Nature|1
environments.slash|SFEnvironmentsSlash|Nature|1
environments|SFEnvironments|Nature|1
equal.circle.fill|SFEqualCircleFill|Math,Multicolor|0
equal.circle|SFEqualCircle|Draw,Math,Variable|0
equal.square.fill|SFEqualSquareFill|Math,Multicolor|0
equal.square|SFEqualSquare|Draw,Math|0
equal|SFEqual|Math|0
eraser.badge.xmark.fill|SFEraserBadgeXmarkFill|Multicolor|0
eraser.badge.xmark|SFEraserBadgeXmark|Multicolor|0
eraser.fill|SFEraserFill|Editing,Objects & Tools|0
eraser.line.dashed.fill|SFEraserLineDashedFill|Editing,Objects & Tools|0
eraser.line.dashed|SFEraserLineDashed|Editing,Objects & Tools|0
eraser.slash.fill|SFEraserSlashFill|Draw|0
eraser.slash|SFEraserSlash|Draw|0
eraser|SFEraser|Editing,Objects & Tools|0
eraser.trianglebadge.exclamationmark.fill|SFEraserTrianglebadgeExclamationmarkFill|Multicolor|0
eraser.trianglebadge.exclamationmark|SFEraserTrianglebadgeExclamationmark|Multicolor|0
escape|SFEscape|Draw,Keyboard|0
esim.fill|SFEsimFill|Objects & Tools|0
esim|SFEsim|Objects & Tools|0
eurosign.arrow.trianglehead.counterclockwise.rotate.90|SFEurosignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
eurosign.bank.building.fill|SFEurosignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
eurosign.bank.building|SFEurosignBankBuilding|Commerce,Objects & Tools|0
eurosign.circle.fill|SFEurosignCircleFill|Commerce,Indices,Multicolor|0
eurosign.circle|SFEurosignCircle|Commerce,Draw,Indices,Variable|0
eurosign.gauge.chart.lefthalf.righthalf|SFEurosignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
eurosign.gauge.chart.leftthird.topthird.rightthird|SFEurosignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
eurosign.ring.dashed|SFEurosignRingDashed|Commerce,Home,Variable|0
eurosign.ring|SFEurosignRing|Commerce,Draw,Home|0
eurosign.square.fill|SFEurosignSquareFill|Commerce,Indices,Multicolor|0
eurosign.square|SFEurosignSquare|Commerce,Draw,Indices|0
eurosign|SFEurosign|Commerce,Indices|0
eurozonesign.arrow.trianglehead.counterclockwise.rotate.90|SFEurozonesignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
eurozonesign.bank.building.fill|SFEurozonesignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
eurozonesign.bank.building|SFEurozonesignBankBuilding|Commerce,Objects & Tools|0
eurozonesign.circle.fill|SFEurozonesignCircleFill|Commerce,Indices,Multicolor|0
eurozonesign.circle|SFEurozonesignCircle|Commerce,Draw,Indices,Variable|0
eurozonesign.gauge.chart.lefthalf.righthalf|SFEurozonesignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
eurozonesign.gauge.chart.leftthird.topthird.rightthird|SFEurozonesignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
eurozonesign.ring.dashed|SFEurozonesignRingDashed|Commerce,Home,Variable|0
eurozonesign.ring|SFEurozonesignRing|Commerce,Draw,Home|0
eurozonesign.square.fill|SFEurozonesignSquareFill|Commerce,Indices,Multicolor|0
eurozonesign.square|SFEurozonesignSquare|Commerce,Draw,Indices|0
eurozonesign|SFEurozonesign|Commerce,Indices|0
ev.charger.arrowtriangle.left.fill|SFEvChargerArrowtriangleLeftFill|Automotive,Objects & Tools,Transportation|0
ev.charger.arrowtriangle.left|SFEvChargerArrowtriangleLeft|Automotive,Objects & Tools,Transportation|0
ev.charger.arrowtriangle.right.fill|SFEvChargerArrowtriangleRightFill|Automotive,Objects & Tools,Transportation|0
ev.charger.arrowtriangle.right|SFEvChargerArrowtriangleRight|Automotive,Objects & Tools,Transportation|0
ev.charger.exclamationmark.fill|SFEvChargerExclamationmarkFill|Automotive,Objects & Tools,Transportation|0
ev.charger.exclamationmark|SFEvChargerExclamationmark|Automotive,Objects & Tools,Transportation|0
ev.charger.fill|SFEvChargerFill|Automotive,Objects & Tools,Transportation|0
ev.charger.slash.fill|SFEvChargerSlashFill|Automotive,Draw,Objects & Tools,Transportation|0
ev.charger.slash|SFEvChargerSlash|Automotive,Draw,Objects & Tools,Transportation|0
ev.charger|SFEvCharger|Automotive,Objects & Tools,Transportation|0
ev.plug.ac.gb.t.fill|SFEvPlugAcGbTFill|Automotive,Maps,Objects & Tools|0
ev.plug.ac.gb.t|SFEvPlugAcGbT|Automotive,Maps,Objects & Tools|0
ev.plug.ac.type.1.fill|SFEvPlugAcType1Fill|Automotive,Maps,Objects & Tools|0
ev.plug.ac.type.1|SFEvPlugAcType1|Automotive,Maps,Objects & Tools|0
ev.plug.ac.type.2.fill|SFEvPlugAcType2Fill|Automotive,Maps,Objects & Tools|0
ev.plug.ac.type.2|SFEvPlugAcType2|Automotive,Maps,Objects & Tools|0
ev.plug.dc.ccs1.fill|SFEvPlugDcCcs1Fill|Automotive,Maps,Objects & Tools|0
ev.plug.dc.ccs1|SFEvPlugDcCcs1|Automotive,Maps,Objects & Tools|0
ev.plug.dc.ccs2.fill|SFEvPlugDcCcs2Fill|Automotive,Maps,Objects & Tools|0
ev.plug.dc.ccs2|SFEvPlugDcCcs2|Automotive,Maps,Objects & Tools|0
ev.plug.dc.chademo.fill|SFEvPlugDcChademoFill|Automotive,Maps,Objects & Tools|0
ev.plug.dc.chademo|SFEvPlugDcChademo|Automotive,Maps,Objects & Tools|0
ev.plug.dc.gb.t.fill|SFEvPlugDcGbTFill|Automotive,Maps,Objects & Tools|0
ev.plug.dc.gb.t|SFEvPlugDcGbT|Automotive,Maps,Objects & Tools|0
ev.plug.dc.nacs.fill|SFEvPlugDcNacsFill|Automotive,Maps,Objects & Tools|0
ev.plug.dc.nacs|SFEvPlugDcNacs|Automotive,Maps,Objects & Tools|0
exclamationmark.2|SFExclamationmark2|Multicolor|0
exclamationmark.3|SFExclamationmark3|Multicolor|0
exclamationmark.applewatch|SFExclamationmarkApplewatch|Devices|1
exclamationmark.arrow.trianglehead.2.clockwise.rotate.90|SFExclamationmarkArrowTrianglehead2ClockwiseRotate90|Arrows,Draw|0
exclamationmark.arrow.trianglehead.counterclockwise.rotate.90|SFExclamationmarkArrowTriangleheadCounterclockwiseRotate90|Arrows,Draw,Time|0
exclamationmark.brakesignal|SFExclamationmarkBrakesignal|Automotive,Multicolor|0
exclamationmark.bubble.circle.fill|SFExclamationmarkBubbleCircleFill|Communication,Maps,Multicolor,Privacy & Security|0
exclamationmark.bubble.circle|SFExclamationmarkBubbleCircle|Communication,Draw,Maps,Privacy & Security,Variable|0
exclamationmark.bubble.fill|SFExclamationmarkBubbleFill|Communication,Maps,Multicolor,Privacy & Security|0
exclamationmark.bubble|SFExclamationmarkBubble|Communication,Maps,Privacy & Security|0
exclamationmark.circle.fill|SFExclamationmarkCircleFill|Indices,Multicolor|0
exclamationmark.circle|SFExclamationmarkCircle|Draw,Indices,Multicolor,Variable|0
exclamationmark.icloud.fill|SFExclamationmarkIcloudFill|Connectivity,Multicolor|1
exclamationmark.icloud|SFExclamationmarkIcloud|Connectivity|1
exclamationmark.lock.fill|SFExclamationmarkLockFill|Objects & Tools,Privacy & Security|0
exclamationmark.lock|SFExclamationmarkLock|Objects & Tools,Privacy & Security|0
exclamationmark.magnifyingglass|SFExclamationmarkMagnifyingglass|Objects & Tools|0
exclamationmark.message.fill|SFExclamationmarkMessageFill|Communication,Multicolor|1
exclamationmark.message|SFExclamationmarkMessage|Communication|1
exclamationmark.octagon.fill|SFExclamationmarkOctagonFill|Multicolor|0
exclamationmark.octagon|SFExclamationmarkOctagon|Multicolor|0
exclamationmark.questionmark|SFExclamationmarkQuestionmark||0
exclamationmark.shield.fill|SFExclamationmarkShieldFill|Multicolor,Objects & Tools,Privacy & Security|0
exclamationmark.shield|SFExclamationmarkShield|Objects & Tools,Privacy & Security|0
exclamationmark.square.fill|SFExclamationmarkSquareFill|Indices,Multicolor|0
exclamationmark.square|SFExclamationmarkSquare|Draw,Indices,Multicolor|0
exclamationmark|SFExclamationmark|Multicolor|0
exclamationmark.tirepressure|SFExclamationmarkTirepressure|Automotive,Multicolor|0
exclamationmark.transmission|SFExclamationmarkTransmission|Automotive|0
exclamationmark.triangle.fill|SFExclamationmarkTriangleFill|Automotive,Multicolor,Privacy & Security|0
exclamationmark.triangle|SFExclamationmarkTriangle|Automotive,Multicolor,Privacy & Security|0
exclamationmark.triangle.text.page.fill|SFExclamationmarkTriangleTextPageFill||0
exclamationmark.triangle.text.page|SFExclamationmarkTriangleTextPage||0
exclamationmark.warninglight.fill|SFExclamationmarkWarninglightFill|Automotive,Draw,Multicolor|0
exclamationmark.warninglight|SFExclamationmarkWarninglight|Automotive,Draw,Multicolor|0
externaldrive.badge.checkmark|SFExternaldriveBadgeCheckmark|Multicolor,Objects & Tools|0
externaldrive.badge.exclamationmark|SFExternaldriveBadgeExclamationmark|Multicolor,Objects & Tools|0
externaldrive.badge.icloud|SFExternaldriveBadgeIcloud|Multicolor,Objects & Tools|0
externaldrive.badge.minus|SFExternaldriveBadgeMinus|Multicolor,Objects & Tools|0
externaldrive.badge.person.crop|SFExternaldriveBadgePersonCrop|Human,Multicolor,Objects & Tools|0
externaldrive.badge.plus|SFExternaldriveBadgePlus|Multicolor,Objects & Tools|0
externaldrive.badge.questionmark|SFExternaldriveBadgeQuestionmark|Multicolor,Objects & Tools|0
externaldrive.badge.timemachine|SFExternaldriveBadgeTimemachine|Objects & Tools|0
externaldrive.badge.wifi|SFExternaldriveBadgeWifi|Multicolor,Objects & Tools,Variable|0
externaldrive.badge.xmark|SFExternaldriveBadgeXmark|Multicolor,Objects & Tools|0
externaldrive.connected.to.line.below.fill|SFExternaldriveConnectedToLineBelowFill|Connectivity,Objects & Tools|0
externaldrive.connected.to.line.below|SFExternaldriveConnectedToLineBelow|Connectivity,Objects & Tools|0
externaldrive.fill.badge.checkmark|SFExternaldriveFillBadgeCheckmark|Multicolor,Objects & Tools|0
externaldrive.fill.badge.exclamationmark|SFExternaldriveFillBadgeExclamationmark|Multicolor,Objects & Tools|0
externaldrive.fill.badge.icloud|SFExternaldriveFillBadgeIcloud|Multicolor,Objects & Tools|0
externaldrive.fill.badge.minus|SFExternaldriveFillBadgeMinus|Multicolor,Objects & Tools|0
externaldrive.fill.badge.person.crop|SFExternaldriveFillBadgePersonCrop|Human,Multicolor,Objects & Tools|0
externaldrive.fill.badge.plus|SFExternaldriveFillBadgePlus|Multicolor,Objects & Tools|0
externaldrive.fill.badge.questionmark|SFExternaldriveFillBadgeQuestionmark|Multicolor,Objects & Tools|0
externaldrive.fill.badge.timemachine|SFExternaldriveFillBadgeTimemachine|Objects & Tools|0
externaldrive.fill.badge.wifi|SFExternaldriveFillBadgeWifi|Multicolor,Objects & Tools,Variable|0
externaldrive.fill.badge.xmark|SFExternaldriveFillBadgeXmark|Multicolor,Objects & Tools|0
externaldrive.fill|SFExternaldriveFill|Objects & Tools|0
externaldrive.fill.trianglebadge.exclamationmark|SFExternaldriveFillTrianglebadgeExclamationmark|Multicolor,Objects & Tools|0
externaldrive|SFExternaldrive|Objects & Tools|0
externaldrive.trianglebadge.exclamationmark|SFExternaldriveTrianglebadgeExclamationmark|Multicolor,Objects & Tools|0
eye.circle.fill|SFEyeCircleFill|Accessibility,Health,Human,Multicolor,Privacy & Security|0
eye.circle|SFEyeCircle|Accessibility,Draw,Health,Human,Privacy & Security,Variable|0
eye.fill|SFEyeFill|Accessibility,Health,Human,Privacy & Security|0
eye.half.closed.fill|SFEyeHalfClosedFill|Accessibility,Human|0
eye.half.closed|SFEyeHalfClosed|Accessibility,Human|0
eye.slash.circle.fill|SFEyeSlashCircleFill|Accessibility,Human,Multicolor,Privacy & Security|0
eye.slash.circle|SFEyeSlashCircle|Accessibility,Draw,Human,Privacy & Security,Variable|0
eye.slash.fill|SFEyeSlashFill|Accessibility,Draw,Health,Human,Privacy & Security|0
eye.slash|SFEyeSlash|Accessibility,Draw,Health,Human,Privacy & Security|0
eye.square.fill|SFEyeSquareFill|Accessibility,Health,Human,Multicolor,Privacy & Security|0
eye.square|SFEyeSquare|Accessibility,Draw,Health,Human,Privacy & Security|0
eye|SFEye|Accessibility,Health,Human,Privacy & Security|0
eye.trianglebadge.exclamationmark.fill|SFEyeTrianglebadgeExclamationmarkFill|Accessibility,Health,Human,Multicolor,Privacy & Security|0
eye.trianglebadge.exclamationmark|SFEyeTrianglebadgeExclamationmark|Accessibility,Health,Human,Multicolor,Privacy & Security|0
eyebrow|SFEyebrow|Human|0
eyedropper.full|SFEyedropperFull|Editing,Objects & Tools|0
eyedropper.halffull|SFEyedropperHalffull|Editing,Objects & Tools|0
eyedropper|SFEyedropper|Editing,Objects & Tools|0
eyeglasses.slash|SFEyeglassesSlash|Draw,Objects & Tools|0
eyeglasses|SFEyeglasses|Objects & Tools|0
eyes.inverse|SFEyesInverse|Human|0
eyes|SFEyes|Human|0
f.circle.fill|SFFCircleFill|Indices,Multicolor|0
f.circle|SFFCircle|Draw,Indices,Variable|0
f.cursive.circle.fill|SFFCursiveCircleFill|Camera & Photos,Multicolor|0
f.cursive.circle|SFFCursiveCircle|Camera & Photos,Draw,Variable|0
f.cursive.slash|SFFCursiveSlash|Camera & Photos,Draw|0
f.cursive|SFFCursive|Camera & Photos|0
f.square.fill|SFFSquareFill|Indices,Multicolor|0
f.square|SFFSquare|Draw,Indices|0
face.dashed.fill|SFFaceDashedFill|Human|0
face.dashed|SFFaceDashed|Human|0
face.smiling.inverse|SFFaceSmilingInverse|Human,Multicolor|0
face.smiling|SFFaceSmiling|Human|0
faceid|SFFaceid|Draw,Multicolor,Privacy & Security|1
facemask.fill|SFFacemaskFill|Health,Multicolor,Objects & Tools|0
facemask|SFFacemask|Health,Objects & Tools|0
fan.and.light.ceiling.fill|SFFanAndLightCeilingFill|Home,Objects & Tools|0
fan.and.light.ceiling|SFFanAndLightCeiling|Home,Objects & Tools|0
fan.badge.arrow.up.and.down.and.arrow.left.and.right.fill|SFFanBadgeArrowUpAndDownAndArrowLeftAndRightFill|Automotive,Objects & Tools|0
fan.badge.arrow.up.and.down.and.arrow.left.and.right|SFFanBadgeArrowUpAndDownAndArrowLeftAndRight|Automotive,Objects & Tools|0
fan.badge.automatic.fill|SFFanBadgeAutomaticFill|Automotive,Multicolor,Objects & Tools|0
fan.badge.automatic|SFFanBadgeAutomatic|Automotive,Multicolor,Objects & Tools|0
fan.ceiling.fill|SFFanCeilingFill|Home,Objects & Tools|0
fan.ceiling|SFFanCeiling|Home,Objects & Tools|0
fan.circle.fill|SFFanCircleFill|Automotive,Home,Multicolor,Objects & Tools|0
fan.circle|SFFanCircle|Automotive,Draw,Home,Objects & Tools,Variable|0
fan.desk.fill|SFFanDeskFill|Home,Objects & Tools|0
fan.desk|SFFanDesk|Home,Objects & Tools|0
fan.fill|SFFanFill|Automotive,Home,Objects & Tools|0
fan.floor.fill|SFFanFloorFill|Home,Objects & Tools|0
fan.floor|SFFanFloor|Home,Objects & Tools|0
fan.gauge.open|SFFanGaugeOpen|Draw,Variable|0
fan.oscillation.fill|SFFanOscillationFill|Draw,Home,Objects & Tools|0
fan.oscillation|SFFanOscillation|Draw,Home,Objects & Tools|0
fan.slash.fill|SFFanSlashFill|Automotive,Draw,Home,Objects & Tools|0
fan.slash|SFFanSlash|Automotive,Draw,Home,Objects & Tools|0
fan|SFFan|Automotive,Home,Objects & Tools|0
faxmachine.fill|SFFaxmachineFill|Devices,Objects & Tools|0
faxmachine|SFFaxmachine|Devices,Objects & Tools|0
ferry.fill|SFFerryFill|Transportation|0
ferry|SFFerry|Transportation|0
fibrechannel|SFFibrechannel||0
field.of.view.ultrawide.fill|SFFieldOfViewUltrawideFill|Communication|0
field.of.view.ultrawide|SFFieldOfViewUltrawide|Communication,Multicolor|0
field.of.view.wide.fill|SFFieldOfViewWideFill|Communication|0
field.of.view.wide|SFFieldOfViewWide|Communication,Multicolor|0
figure.2.and.child.holdinghands|SFFigure2AndChildHoldinghands|Human|0
figure.2.arms.open|SFFigure2ArmsOpen|Human|0
figure.2.circle.fill|SFFigure2CircleFill|Accessibility,Human,Multicolor|0
figure.2.circle|SFFigure2Circle|Accessibility,Draw,Human,Variable|0
figure.2.left.holdinghands|SFFigure2LeftHoldinghands|Human|0
figure.2.right.holdinghands|SFFigure2RightHoldinghands|Human|0
figure.2|SFFigure2|Accessibility,Human|0
figure.american.football.circle.fill|SFFigureAmericanFootballCircleFill|Fitness,Human,Multicolor|0
figure.american.football.circle|SFFigureAmericanFootballCircle|Draw,Fitness,Human,Variable|0
figure.american.football|SFFigureAmericanFootball|Fitness,Human|0
figure.and.child.holdinghands|SFFigureAndChildHoldinghands|Human,Multicolor|0
figure.archery.circle.fill|SFFigureArcheryCircleFill|Fitness,Human,Multicolor|0
figure.archery.circle|SFFigureArcheryCircle|Draw,Fitness,Human,Variable|0
figure.archery|SFFigureArchery|Fitness,Human|0
figure.arms.open|SFFigureArmsOpen|Human|0
figure.australian.football.circle.fill|SFFigureAustralianFootballCircleFill|Fitness,Human,Multicolor|0
figure.australian.football.circle|SFFigureAustralianFootballCircle|Draw,Fitness,Human,Variable|0
figure.australian.football|SFFigureAustralianFootball|Fitness,Human|0
figure.badminton.circle.fill|SFFigureBadmintonCircleFill|Fitness,Human,Multicolor|0
figure.badminton.circle|SFFigureBadmintonCircle|Draw,Fitness,Human,Variable|0
figure.badminton|SFFigureBadminton|Fitness,Human|0
figure.barre.circle.fill|SFFigureBarreCircleFill|Fitness,Human,Multicolor|0
figure.barre.circle|SFFigureBarreCircle|Draw,Fitness,Human,Variable|0
figure.barre|SFFigureBarre|Fitness,Human|0
figure.baseball.circle.fill|SFFigureBaseballCircleFill|Fitness,Human,Multicolor|0
figure.baseball.circle|SFFigureBaseballCircle|Draw,Fitness,Human,Variable|0
figure.baseball|SFFigureBaseball|Fitness,Human|0
figure.basketball.circle.fill|SFFigureBasketballCircleFill|Fitness,Human,Multicolor|0
figure.basketball.circle|SFFigureBasketballCircle|Draw,Fitness,Human,Variable|0
figure.basketball|SFFigureBasketball|Fitness,Human|0
figure.bowling.circle.fill|SFFigureBowlingCircleFill|Fitness,Human,Multicolor|0
figure.bowling.circle|SFFigureBowlingCircle|Draw,Fitness,Human,Variable|0
figure.bowling|SFFigureBowling|Fitness,Human|0
figure.boxing.circle.fill|SFFigureBoxingCircleFill|Fitness,Human,Multicolor|0
figure.boxing.circle|SFFigureBoxingCircle|Draw,Fitness,Human,Variable|0
figure.boxing|SFFigureBoxing|Fitness,Human|0
figure.child.and.lock.fill|SFFigureChildAndLockFill|Automotive,Human|0
figure.child.and.lock.open.fill|SFFigureChildAndLockOpenFill|Automotive,Human|0
figure.child.and.lock.open|SFFigureChildAndLockOpen|Automotive,Human|0
figure.child.and.lock|SFFigureChildAndLock|Automotive,Human|0
figure.child.circle.fill|SFFigureChildCircleFill|Automotive,Human,Multicolor|0
figure.child.circle|SFFigureChildCircle|Automotive,Draw,Human,Variable|0
figure.child|SFFigureChild|Automotive,Human|0
figure.climbing.circle.fill|SFFigureClimbingCircleFill|Fitness,Human,Multicolor|0
figure.climbing.circle|SFFigureClimbingCircle|Draw,Fitness,Human,Variable|0
figure.climbing|SFFigureClimbing|Fitness,Human|0
figure.cooldown.circle.fill|SFFigureCooldownCircleFill|Fitness,Human,Multicolor|0
figure.cooldown.circle|SFFigureCooldownCircle|Draw,Fitness,Human,Variable|0
figure.cooldown|SFFigureCooldown|Fitness,Human|0
figure.core.training.circle.fill|SFFigureCoreTrainingCircleFill|Fitness,Human,Multicolor|0
figure.core.training.circle|SFFigureCoreTrainingCircle|Draw,Fitness,Human,Variable|0
figure.core.training|SFFigureCoreTraining|Fitness,Human|0
figure.cricket.circle.fill|SFFigureCricketCircleFill|Fitness,Human,Multicolor|0
figure.cricket.circle|SFFigureCricketCircle|Draw,Fitness,Human,Variable|0
figure.cricket|SFFigureCricket|Fitness,Human|0
figure.cross.training.circle.fill|SFFigureCrossTrainingCircleFill|Fitness,Human,Multicolor|0
figure.cross.training.circle|SFFigureCrossTrainingCircle|Draw,Fitness,Human,Variable|0
figure.cross.training|SFFigureCrossTraining|Fitness,Human|0
figure.curling.circle.fill|SFFigureCurlingCircleFill|Fitness,Human,Multicolor|0
figure.curling.circle|SFFigureCurlingCircle|Draw,Fitness,Human,Variable|0
figure.curling|SFFigureCurling|Fitness,Human|0
figure.dance.circle.fill|SFFigureDanceCircleFill|Fitness,Human,Multicolor|0
figure.dance.circle|SFFigureDanceCircle|Draw,Fitness,Human,Variable|0
figure.dance|SFFigureDance|Fitness,Human|0
figure.disc.sports.circle.fill|SFFigureDiscSportsCircleFill|Fitness,Human,Multicolor|0
figure.disc.sports.circle|SFFigureDiscSportsCircle|Draw,Fitness,Human,Variable|0
figure.disc.sports|SFFigureDiscSports|Fitness,Human|0
figure.elliptical.circle.fill|SFFigureEllipticalCircleFill|Fitness,Human,Multicolor|0
figure.elliptical.circle|SFFigureEllipticalCircle|Draw,Fitness,Human,Variable|0
figure.elliptical|SFFigureElliptical|Fitness,Human|0
figure.equestrian.sports.circle.fill|SFFigureEquestrianSportsCircleFill|Fitness,Human,Multicolor|0
figure.equestrian.sports.circle|SFFigureEquestrianSportsCircle|Draw,Fitness,Human,Variable|0
figure.equestrian.sports|SFFigureEquestrianSports|Fitness,Human|0
figure.fall.circle.fill|SFFigureFallCircleFill|Human,Multicolor|0
figure.fall.circle|SFFigureFallCircle|Draw,Human,Variable|0
figure.fall|SFFigureFall|Human|0
figure.fencing.circle.fill|SFFigureFencingCircleFill|Fitness,Human,Multicolor|0
figure.fencing.circle|SFFigureFencingCircle|Draw,Fitness,Human,Variable|0
figure.fencing|SFFigureFencing|Fitness,Human|0
figure.field.hockey.circle.fill|SFFigureFieldHockeyCircleFill|Fitness,Human,Multicolor|0
figure.field.hockey.circle|SFFigureFieldHockeyCircle|Fitness,Human|0
figure.field.hockey|SFFigureFieldHockey|Fitness,Human|0
figure.fishing.circle.fill|SFFigureFishingCircleFill|Fitness,Human,Multicolor|0
figure.fishing.circle|SFFigureFishingCircle|Draw,Fitness,Human,Variable|0
figure.fishing|SFFigureFishing|Fitness,Human|0
figure.flexibility.circle.fill|SFFigureFlexibilityCircleFill|Fitness,Human,Multicolor|0
figure.flexibility.circle|SFFigureFlexibilityCircle|Draw,Fitness,Human,Variable|0
figure.flexibility|SFFigureFlexibility|Fitness,Human|0
figure.golf.circle.fill|SFFigureGolfCircleFill|Fitness,Human,Multicolor|0
figure.golf.circle|SFFigureGolfCircle|Draw,Fitness,Human,Variable|0
figure.golf|SFFigureGolf|Fitness,Human|0
figure.gymnastics.circle.fill|SFFigureGymnasticsCircleFill|Fitness,Human,Multicolor|0
figure.gymnastics.circle|SFFigureGymnasticsCircle|Draw,Fitness,Human,Variable|0
figure.gymnastics|SFFigureGymnastics|Fitness,Human|0
figure.hand.cycling.circle.fill|SFFigureHandCyclingCircleFill|Fitness,Human,Multicolor|0
figure.hand.cycling.circle|SFFigureHandCyclingCircle|Draw,Fitness,Human,Variable|0
figure.hand.cycling|SFFigureHandCycling|Fitness,Human|0
figure.handball.circle.fill|SFFigureHandballCircleFill|Fitness,Human,Multicolor|0
figure.handball.circle|SFFigureHandballCircle|Draw,Fitness,Human,Variable|0
figure.handball|SFFigureHandball|Fitness,Human|0
figure.highintensity.intervaltraining.circle.fill|SFFigureHighintensityIntervaltrainingCircleFill|Fitness,Human,Multicolor|0
figure.highintensity.intervaltraining.circle|SFFigureHighintensityIntervaltrainingCircle|Draw,Fitness,Human,Variable|0
figure.highintensity.intervaltraining|SFFigureHighintensityIntervaltraining|Fitness,Human|0
figure.hiking.circle.fill|SFFigureHikingCircleFill|Fitness,Human,Multicolor|0
figure.hiking.circle|SFFigureHikingCircle|Draw,Fitness,Human,Variable|0
figure.hiking|SFFigureHiking|Fitness,Human|0
figure.hockey.circle.fill|SFFigureHockeyCircleFill|Fitness,Human,Multicolor|0
figure.hockey.circle|SFFigureHockeyCircle|Draw,Fitness,Human,Variable|0
figure.hockey|SFFigureHockey|Fitness,Human|0
figure.hunting.circle.fill|SFFigureHuntingCircleFill|Fitness,Human,Multicolor|0
figure.hunting.circle|SFFigureHuntingCircle|Draw,Fitness,Human,Variable|0
figure.hunting|SFFigureHunting|Fitness,Human|0
figure.ice.hockey.circle.fill|SFFigureIceHockeyCircleFill|Fitness,Human,Multicolor|0
figure.ice.hockey.circle|SFFigureIceHockeyCircle|Fitness,Human|0
figure.ice.hockey|SFFigureIceHockey|Fitness,Human|0
figure.ice.skating.circle.fill|SFFigureIceSkatingCircleFill|Fitness,Human,Multicolor|0
figure.ice.skating.circle|SFFigureIceSkatingCircle|Draw,Fitness,Human,Variable|0
figure.ice.skating|SFFigureIceSkating|Fitness,Human|0
figure.indoor.cycle.circle.fill|SFFigureIndoorCycleCircleFill|Fitness,Human,Multicolor|0
figure.indoor.cycle.circle|SFFigureIndoorCycleCircle|Draw,Fitness,Human,Variable|0
figure.indoor.cycle|SFFigureIndoorCycle|Fitness,Human|0
figure.indoor.rowing.circle.fill|SFFigureIndoorRowingCircleFill|Fitness,Human,Multicolor|0
figure.indoor.rowing.circle|SFFigureIndoorRowingCircle|Draw,Fitness,Human,Variable|0
figure.indoor.rowing|SFFigureIndoorRowing|Fitness,Human|0
figure.indoor.soccer.circle.fill|SFFigureIndoorSoccerCircleFill|Fitness,Human,Multicolor|0
figure.indoor.soccer.circle|SFFigureIndoorSoccerCircle|Draw,Fitness,Human,Variable|0
figure.indoor.soccer|SFFigureIndoorSoccer|Fitness,Human|0
figure.jumprope.circle.fill|SFFigureJumpropeCircleFill|Fitness,Human,Multicolor|0
figure.jumprope.circle|SFFigureJumpropeCircle|Draw,Fitness,Human,Variable|0
figure.jumprope|SFFigureJumprope|Fitness,Human|0
figure.kickboxing.circle.fill|SFFigureKickboxingCircleFill|Fitness,Human,Multicolor|0
figure.kickboxing.circle|SFFigureKickboxingCircle|Draw,Fitness,Human,Variable|0
figure.kickboxing|SFFigureKickboxing|Fitness,Human|0
figure.lacrosse.circle.fill|SFFigureLacrosseCircleFill|Fitness,Human,Multicolor|0
figure.lacrosse.circle|SFFigureLacrosseCircle|Draw,Fitness,Human,Variable|0
figure.lacrosse|SFFigureLacrosse|Fitness,Human|0
figure.martial.arts.circle.fill|SFFigureMartialArtsCircleFill|Fitness,Human,Multicolor|0
figure.martial.arts.circle|SFFigureMartialArtsCircle|Draw,Fitness,Human,Variable|0
figure.martial.arts|SFFigureMartialArts|Fitness,Human|0
figure.mind.and.body.circle.fill|SFFigureMindAndBodyCircleFill|Fitness,Human,Multicolor|0
figure.mind.and.body.circle|SFFigureMindAndBodyCircle|Draw,Fitness,Human,Variable|0
figure.mind.and.body|SFFigureMindAndBody|Fitness,Human|0
figure.mixed.cardio.circle.fill|SFFigureMixedCardioCircleFill|Fitness,Human,Multicolor|0
figure.mixed.cardio.circle|SFFigureMixedCardioCircle|Draw,Fitness,Human,Variable|0
figure.mixed.cardio|SFFigureMixedCardio|Fitness,Human|0
figure.open.water.swim.circle.fill|SFFigureOpenWaterSwimCircleFill|Fitness,Human,Multicolor|0
figure.open.water.swim.circle|SFFigureOpenWaterSwimCircle|Draw,Fitness,Human,Variable|0
figure.open.water.swim|SFFigureOpenWaterSwim|Fitness,Human|0
figure.outdoor.cycle.circle.fill|SFFigureOutdoorCycleCircleFill|Fitness,Human,Multicolor|0
figure.outdoor.cycle.circle|SFFigureOutdoorCycleCircle|Draw,Fitness,Human,Variable|0
figure.outdoor.cycle|SFFigureOutdoorCycle|Fitness,Human|0
figure.outdoor.rowing.circle.fill|SFFigureOutdoorRowingCircleFill|Fitness,Human,Multicolor|0
figure.outdoor.rowing.circle|SFFigureOutdoorRowingCircle|Draw,Fitness,Human,Variable|0
figure.outdoor.rowing|SFFigureOutdoorRowing|Fitness,Human|0
figure.outdoor.soccer.circle.fill|SFFigureOutdoorSoccerCircleFill|Fitness,Human,Multicolor|0
figure.outdoor.soccer.circle|SFFigureOutdoorSoccerCircle|Draw,Fitness,Human,Variable|0
figure.outdoor.soccer|SFFigureOutdoorSoccer|Fitness,Human|0
figure.pickleball.circle.fill|SFFigurePickleballCircleFill|Fitness,Human,Multicolor|0
figure.pickleball.circle|SFFigurePickleballCircle|Draw,Fitness,Human,Variable|0
figure.pickleball|SFFigurePickleball|Fitness,Human|0
figure.pilates.circle.fill|SFFigurePilatesCircleFill|Fitness,Human,Multicolor|0
figure.pilates.circle|SFFigurePilatesCircle|Draw,Fitness,Human,Variable|0
figure.pilates|SFFigurePilates|Fitness,Human|0
figure.play.circle.fill|SFFigurePlayCircleFill|Fitness,Human,Multicolor|0
figure.play.circle|SFFigurePlayCircle|Draw,Fitness,Human,Variable|0
figure.play|SFFigurePlay|Fitness,Human|0
figure.pool.swim.circle.fill|SFFigurePoolSwimCircleFill|Fitness,Human,Multicolor|0
figure.pool.swim.circle|SFFigurePoolSwimCircle|Draw,Fitness,Human,Variable|0
figure.pool.swim|SFFigurePoolSwim|Fitness,Human|0
figure.racquetball.circle.fill|SFFigureRacquetballCircleFill|Fitness,Human,Multicolor|0
figure.racquetball.circle|SFFigureRacquetballCircle|Draw,Fitness,Human,Variable|0
figure.racquetball|SFFigureRacquetball|Fitness,Human|0
figure.roll.circle.fill|SFFigureRollCircleFill|Accessibility,Fitness,Human,Multicolor|0
figure.roll.circle|SFFigureRollCircle|Accessibility,Draw,Fitness,Human,Variable|0
figure.roll.runningpace.circle.fill|SFFigureRollRunningpaceCircleFill|Accessibility,Fitness,Human,Multicolor|0
figure.roll.runningpace.circle|SFFigureRollRunningpaceCircle|Accessibility,Draw,Fitness,Human,Variable|0
figure.roll.runningpace|SFFigureRollRunningpace|Accessibility,Fitness,Human|0
figure.roll|SFFigureRoll|Accessibility,Fitness,Human|0
figure.rolling.circle.fill|SFFigureRollingCircleFill|Fitness,Human,Multicolor|0
figure.rolling.circle|SFFigureRollingCircle|Draw,Fitness,Human,Variable|0
figure.rolling|SFFigureRolling|Fitness,Human|0
figure.rugby.circle.fill|SFFigureRugbyCircleFill|Fitness,Human,Multicolor|0
figure.rugby.circle|SFFigureRugbyCircle|Draw,Fitness,Human,Variable|0
figure.rugby|SFFigureRugby|Fitness,Human|0
figure.run.circle.fill|SFFigureRunCircleFill|Fitness,Human,Multicolor|0
figure.run.circle|SFFigureRunCircle|Draw,Fitness,Human,Variable|0
figure.run.square.stack.fill|SFFigureRunSquareStackFill|Fitness,Human,Multicolor|0
figure.run.square.stack|SFFigureRunSquareStack|Fitness,Human|0
figure.run|SFFigureRun|Fitness,Human|0
figure.run.treadmill.circle.fill|SFFigureRunTreadmillCircleFill|Fitness,Human,Multicolor|0
figure.run.treadmill.circle|SFFigureRunTreadmillCircle|Draw,Fitness,Human,Variable|0
figure.run.treadmill|SFFigureRunTreadmill|Fitness,Human|0
figure.sailing.circle.fill|SFFigureSailingCircleFill|Fitness,Human,Multicolor|0
figure.sailing.circle|SFFigureSailingCircle|Draw,Fitness,Human,Variable|0
figure.sailing|SFFigureSailing|Fitness,Human|0
figure.seated.seatbelt.and.airbag.off|SFFigureSeatedSeatbeltAndAirbagOff|Automotive,Human,Multicolor|0
figure.seated.seatbelt.and.airbag.on|SFFigureSeatedSeatbeltAndAirbagOn|Automotive,Human,Multicolor|0
figure.seated.seatbelt.left.drive.seats.1.1.fill|SFFigureSeatedSeatbeltLeftDriveSeats11Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.1.1|SFFigureSeatedSeatbeltLeftDriveSeats11|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.1.2.fill|SFFigureSeatedSeatbeltLeftDriveSeats12Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.1.2|SFFigureSeatedSeatbeltLeftDriveSeats12|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.1.fill|SFFigureSeatedSeatbeltLeftDriveSeats1Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.1|SFFigureSeatedSeatbeltLeftDriveSeats1|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.2.2.fill|SFFigureSeatedSeatbeltLeftDriveSeats222Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.2.2|SFFigureSeatedSeatbeltLeftDriveSeats222|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.2.3.fill|SFFigureSeatedSeatbeltLeftDriveSeats223Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.2.3|SFFigureSeatedSeatbeltLeftDriveSeats223|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.2.fill|SFFigureSeatedSeatbeltLeftDriveSeats22Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.2|SFFigureSeatedSeatbeltLeftDriveSeats22|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.3.2.fill|SFFigureSeatedSeatbeltLeftDriveSeats232Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.3.2|SFFigureSeatedSeatbeltLeftDriveSeats232|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.3.3.fill|SFFigureSeatedSeatbeltLeftDriveSeats233Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.3.3|SFFigureSeatedSeatbeltLeftDriveSeats233|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.3.fill|SFFigureSeatedSeatbeltLeftDriveSeats23Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.3|SFFigureSeatedSeatbeltLeftDriveSeats23|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2.fill|SFFigureSeatedSeatbeltLeftDriveSeats2Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.2|SFFigureSeatedSeatbeltLeftDriveSeats2|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.3.3.3.fill|SFFigureSeatedSeatbeltLeftDriveSeats333Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.3.3.3|SFFigureSeatedSeatbeltLeftDriveSeats333|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.3.3.fill|SFFigureSeatedSeatbeltLeftDriveSeats33Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.3.3|SFFigureSeatedSeatbeltLeftDriveSeats33|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.3.fill|SFFigureSeatedSeatbeltLeftDriveSeats3Fill|Automotive,Human|0
figure.seated.seatbelt.left.drive.seats.3|SFFigureSeatedSeatbeltLeftDriveSeats3|Automotive,Human|0
figure.seated.seatbelt|SFFigureSeatedSeatbelt|Automotive,Human,Multicolor|0
figure.seated.side.left.air.distribution.indirect|SFFigureSeatedSideLeftAirDistributionIndirect|Automotive,Human|0
figure.seated.side.left.air.distribution.lower.angled.and.upper.angled|SFFigureSeatedSideLeftAirDistributionLowerAngledAndUpperAngled|Automotive,Human|0
figure.seated.side.left.air.distribution.lower|SFFigureSeatedSideLeftAirDistributionLower|Automotive,Human|0
figure.seated.side.left.air.distribution.middle.and.lower.angled|SFFigureSeatedSideLeftAirDistributionMiddleAndLowerAngled|Automotive,Human|0
figure.seated.side.left.air.distribution.middle.and.lower|SFFigureSeatedSideLeftAirDistributionMiddleAndLower|Automotive,Human|0
figure.seated.side.left.air.distribution.middle|SFFigureSeatedSideLeftAirDistributionMiddle|Automotive,Human|0
figure.seated.side.left.air.distribution.upper.and.middle.and.lower|SFFigureSeatedSideLeftAirDistributionUpperAndMiddleAndLower|Automotive,Human|0
figure.seated.side.left.air.distribution.upper.angled.and.dottedline.and.lower.angled|SFFigureSeatedSideLeftAirDistributionUpperAngledAndDottedlineAndLowerAngled|Automotive,Human|0
figure.seated.side.left.air.distribution.upper.angled.and.lower.angled|SFFigureSeatedSideLeftAirDistributionUpperAngledAndLowerAngled|Automotive,Human|0
figure.seated.side.left.air.distribution.upper.angled.and.middle.and.lower.angled|SFFigureSeatedSideLeftAirDistributionUpperAngledAndMiddleAndLowerAngled|Automotive,Human|0
figure.seated.side.left.air.distribution.upper.angled.and.middle|SFFigureSeatedSideLeftAirDistributionUpperAngledAndMiddle|Automotive,Human|0
figure.seated.side.left.air.distribution.upper|SFFigureSeatedSideLeftAirDistributionUpper|Automotive,Human|0
figure.seated.side.left.airbag.off.2|SFFigureSeatedSideLeftAirbagOff2|Automotive,Human,Multicolor|0
figure.seated.side.left.airbag.off|SFFigureSeatedSideLeftAirbagOff|Automotive,Human,Multicolor|0
figure.seated.side.left.airbag.on.2|SFFigureSeatedSideLeftAirbagOn2|Automotive,Human,Multicolor|0
figure.seated.side.left.airbag.on|SFFigureSeatedSideLeftAirbagOn|Automotive,Human,Multicolor|0
figure.seated.side.left.automatic|SFFigureSeatedSideLeftAutomatic|Automotive,Human|0
figure.seated.side.left.fan|SFFigureSeatedSideLeftFan|Automotive,Human|0
figure.seated.side.left.steeringwheel|SFFigureSeatedSideLeftSteeringwheel|Automotive,Human|0
figure.seated.side.left|SFFigureSeatedSideLeft|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves.air.distribution.lower|SFFigureSeatedSideLeftWindshieldFrontAndHeatWavesAirDistributionLower|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves.air.distribution.middle.and.lower|SFFigureSeatedSideLeftWindshieldFrontAndHeatWavesAirDistributionMiddleAndLower|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves.air.distribution.middle|SFFigureSeatedSideLeftWindshieldFrontAndHeatWavesAirDistributionMiddle|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves.air.distribution.upper.and.lower|SFFigureSeatedSideLeftWindshieldFrontAndHeatWavesAirDistributionUpperAndLower|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves.air.distribution.upper.and.middle.and.lower|SFFigureSeatedSideLeftWindshieldFrontAndHeatWavesAirDistributionUpperAndMiddleAndLower|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves.air.distribution.upper.and.middle|SFFigureSeatedSideLeftWindshieldFrontAndHeatWavesAirDistributionUpperAndMiddle|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves.air.distribution.upper|SFFigureSeatedSideLeftWindshieldFrontAndHeatWavesAirDistributionUpper|Automotive,Human|0
figure.seated.side.left.windshield.front.and.heat.waves|SFFigureSeatedSideLeftWindshieldFrontAndHeatWaves|Automotive,Human|0
figure.seated.side.right.air.distribution.indirect|SFFigureSeatedSideRightAirDistributionIndirect|Automotive,Human|0
figure.seated.side.right.air.distribution.lower.angled.and.upper.angled|SFFigureSeatedSideRightAirDistributionLowerAngledAndUpperAngled|Automotive,Human|0
figure.seated.side.right.air.distribution.lower|SFFigureSeatedSideRightAirDistributionLower|Automotive,Human|0
figure.seated.side.right.air.distribution.middle.and.lower.angled|SFFigureSeatedSideRightAirDistributionMiddleAndLowerAngled|Automotive,Human|0
figure.seated.side.right.air.distribution.middle.and.lower|SFFigureSeatedSideRightAirDistributionMiddleAndLower|Automotive,Human|0
figure.seated.side.right.air.distribution.middle|SFFigureSeatedSideRightAirDistributionMiddle|Automotive,Human|0
figure.seated.side.right.air.distribution.upper.and.middle.and.lower|SFFigureSeatedSideRightAirDistributionUpperAndMiddleAndLower|Automotive,Human|0
figure.seated.side.right.air.distribution.upper.angled.and.dottedline.and.lower.angled|SFFigureSeatedSideRightAirDistributionUpperAngledAndDottedlineAndLowerAngled|Automotive,Human|0
figure.seated.side.right.air.distribution.upper.angled.and.lower.angled|SFFigureSeatedSideRightAirDistributionUpperAngledAndLowerAngled|Automotive,Human|0
figure.seated.side.right.air.distribution.upper.angled.and.middle.and.lower.angled|SFFigureSeatedSideRightAirDistributionUpperAngledAndMiddleAndLowerAngled|Automotive,Human|0
figure.seated.side.right.air.distribution.upper.angled.and.middle|SFFigureSeatedSideRightAirDistributionUpperAngledAndMiddle|Automotive,Human|0
figure.seated.side.right.air.distribution.upper|SFFigureSeatedSideRightAirDistributionUpper|Automotive,Human|0
figure.seated.side.right.airbag.off.2|SFFigureSeatedSideRightAirbagOff2|Automotive,Human,Multicolor|0
figure.seated.side.right.airbag.off|SFFigureSeatedSideRightAirbagOff|Automotive,Human,Multicolor|0
figure.seated.side.right.airbag.on.2|SFFigureSeatedSideRightAirbagOn2|Automotive,Human,Multicolor|0
figure.seated.side.right.airbag.on|SFFigureSeatedSideRightAirbagOn|Automotive,Human,Multicolor|0
figure.seated.side.right.automatic|SFFigureSeatedSideRightAutomatic|Automotive,Human|0
figure.seated.side.right.child.lap|SFFigureSeatedSideRightChildLap|Human|0
figure.seated.side.right.fan|SFFigureSeatedSideRightFan|Automotive,Human|0
figure.seated.side.right.steeringwheel|SFFigureSeatedSideRightSteeringwheel|Automotive,Human|0
figure.seated.side.right|SFFigureSeatedSideRight|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves.air.distribution.lower|SFFigureSeatedSideRightWindshieldFrontAndHeatWavesAirDistributionLower|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves.air.distribution.middle.and.lower|SFFigureSeatedSideRightWindshieldFrontAndHeatWavesAirDistributionMiddleAndLower|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves.air.distribution.middle|SFFigureSeatedSideRightWindshieldFrontAndHeatWavesAirDistributionMiddle|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves.air.distribution.upper.and.lower|SFFigureSeatedSideRightWindshieldFrontAndHeatWavesAirDistributionUpperAndLower|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves.air.distribution.upper.and.middle.and.lower|SFFigureSeatedSideRightWindshieldFrontAndHeatWavesAirDistributionUpperAndMiddleAndLower|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves.air.distribution.upper.and.middle|SFFigureSeatedSideRightWindshieldFrontAndHeatWavesAirDistributionUpperAndMiddle|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves.air.distribution.upper|SFFigureSeatedSideRightWindshieldFrontAndHeatWavesAirDistributionUpper|Automotive,Human|0
figure.seated.side.right.windshield.front.and.heat.waves|SFFigureSeatedSideRightWindshieldFrontAndHeatWaves|Automotive,Human|0
figure.skateboarding.circle.fill|SFFigureSkateboardingCircleFill|Fitness,Human,Multicolor|0
figure.skateboarding.circle|SFFigureSkateboardingCircle|Draw,Fitness,Human,Variable|0
figure.skateboarding|SFFigureSkateboarding|Fitness,Human|0
figure.skiing.crosscountry.circle.fill|SFFigureSkiingCrosscountryCircleFill|Fitness,Human,Multicolor|0
figure.skiing.crosscountry.circle|SFFigureSkiingCrosscountryCircle|Draw,Fitness,Human,Variable|0
figure.skiing.crosscountry|SFFigureSkiingCrosscountry|Fitness,Human|0
figure.skiing.downhill.circle.fill|SFFigureSkiingDownhillCircleFill|Fitness,Human,Multicolor|0
figure.skiing.downhill.circle|SFFigureSkiingDownhillCircle|Draw,Fitness,Human,Variable|0
figure.skiing.downhill|SFFigureSkiingDownhill|Fitness,Human|0
figure.snowboarding.circle.fill|SFFigureSnowboardingCircleFill|Fitness,Human,Multicolor|0
figure.snowboarding.circle|SFFigureSnowboardingCircle|Draw,Fitness,Human,Variable|0
figure.snowboarding|SFFigureSnowboarding|Fitness,Human|0
figure.socialdance.circle.fill|SFFigureSocialdanceCircleFill|Fitness,Human,Multicolor|0
figure.socialdance.circle|SFFigureSocialdanceCircle|Draw,Fitness,Human,Variable|0
figure.socialdance|SFFigureSocialdance|Fitness,Human|0
figure.softball.circle.fill|SFFigureSoftballCircleFill|Fitness,Human,Multicolor|0
figure.softball.circle|SFFigureSoftballCircle|Draw,Fitness,Human,Variable|0
figure.softball|SFFigureSoftball|Fitness,Human|0
figure.squash.circle.fill|SFFigureSquashCircleFill|Fitness,Human,Multicolor|0
figure.squash.circle|SFFigureSquashCircle|Draw,Fitness,Human,Variable|0
figure.squash|SFFigureSquash|Fitness,Human|0
figure.stair.stepper.circle.fill|SFFigureStairStepperCircleFill|Fitness,Human,Multicolor|0
figure.stair.stepper.circle|SFFigureStairStepperCircle|Draw,Fitness,Human,Variable|0
figure.stair.stepper|SFFigureStairStepper|Fitness,Human|0
figure.stairs.circle.fill|SFFigureStairsCircleFill|Fitness,Human,Multicolor|0
figure.stairs.circle|SFFigureStairsCircle|Draw,Fitness,Human,Variable|0
figure.stairs|SFFigureStairs|Fitness,Human|0
figure.stand.dress.line.vertical.figure|SFFigureStandDressLineVerticalFigure|Human|0
figure.stand.dress|SFFigureStandDress|Human|0
figure.stand.line.dotted.figure.stand|SFFigureStandLineDottedFigureStand|Accessibility,Human|0
figure.stand|SFFigureStand|Human|0
figure.step.training.circle.fill|SFFigureStepTrainingCircleFill|Fitness,Human,Multicolor|0
figure.step.training.circle|SFFigureStepTrainingCircle|Draw,Fitness,Human,Variable|0
figure.step.training|SFFigureStepTraining|Fitness,Human|0
figure.strengthtraining.functional.circle.fill|SFFigureStrengthtrainingFunctionalCircleFill|Fitness,Human,Multicolor|0
figure.strengthtraining.functional.circle|SFFigureStrengthtrainingFunctionalCircle|Draw,Fitness,Human,Variable|0
figure.strengthtraining.functional|SFFigureStrengthtrainingFunctional|Fitness,Human|0
figure.strengthtraining.traditional.circle.fill|SFFigureStrengthtrainingTraditionalCircleFill|Fitness,Human,Multicolor|0
figure.strengthtraining.traditional.circle|SFFigureStrengthtrainingTraditionalCircle|Draw,Fitness,Human,Variable|0
figure.strengthtraining.traditional|SFFigureStrengthtrainingTraditional|Fitness,Human|0
figure.surfing.circle.fill|SFFigureSurfingCircleFill|Fitness,Human,Multicolor|0
figure.surfing.circle|SFFigureSurfingCircle|Draw,Fitness,Human,Variable|0
figure.surfing|SFFigureSurfing|Fitness,Human|0
figure|SFFigure|Accessibility,Human|0
figure.table.tennis.circle.fill|SFFigureTableTennisCircleFill|Fitness,Human,Multicolor|0
figure.table.tennis.circle|SFFigureTableTennisCircle|Draw,Fitness,Human,Variable|0
figure.table.tennis|SFFigureTableTennis|Fitness,Human|0
figure.taichi.circle.fill|SFFigureTaichiCircleFill|Fitness,Human,Multicolor|0
figure.taichi.circle|SFFigureTaichiCircle|Draw,Fitness,Human,Variable|0
figure.taichi|SFFigureTaichi|Fitness,Human|0
figure.tennis.circle.fill|SFFigureTennisCircleFill|Fitness,Human,Multicolor|0
figure.tennis.circle|SFFigureTennisCircle|Draw,Fitness,Human,Variable|0
figure.tennis|SFFigureTennis|Fitness,Human|0
figure.track.and.field.circle.fill|SFFigureTrackAndFieldCircleFill|Fitness,Human,Multicolor|0
figure.track.and.field.circle|SFFigureTrackAndFieldCircle|Draw,Fitness,Human,Variable|0
figure.track.and.field|SFFigureTrackAndField|Fitness,Human|0
figure.volleyball.circle.fill|SFFigureVolleyballCircleFill|Fitness,Human,Multicolor|0
figure.volleyball.circle|SFFigureVolleyballCircle|Draw,Fitness,Human,Variable|0
figure.volleyball|SFFigureVolleyball|Fitness,Human|0
figure.walk.arrival|SFFigureWalkArrival|Home,Human|0
figure.walk.circle.fill|SFFigureWalkCircleFill|Fitness,Human,Maps,Multicolor,Transportation|0
figure.walk.circle|SFFigureWalkCircle|Draw,Fitness,Human,Maps,Transportation,Variable|0
figure.walk.departure|SFFigureWalkDeparture|Home,Human|0
figure.walk.diamond.fill|SFFigureWalkDiamondFill|Fitness,Human,Maps,Multicolor,Transportation|0
figure.walk.diamond|SFFigureWalkDiamond|Fitness,Human,Maps,Transportation|0
figure.walk.motion|SFFigureWalkMotion|Fitness,Human|0
figure.walk.motion.trianglebadge.exclamationmark|SFFigureWalkMotionTrianglebadgeExclamationmark|Fitness,Human,Multicolor|0
figure.walk.suitcase.rolling.circle.fill|SFFigureWalkSuitcaseRollingCircleFill|Human,Maps,Multicolor,Transportation|0
figure.walk.suitcase.rolling.circle|SFFigureWalkSuitcaseRollingCircle|Draw,Human,Maps,Transportation,Variable|0
figure.walk.suitcase.rolling|SFFigureWalkSuitcaseRolling|Human,Maps,Transportation|0
figure.walk|SFFigureWalk|Fitness,Human,Maps,Transportation|0
figure.walk.treadmill.circle.fill|SFFigureWalkTreadmillCircleFill|Fitness,Human,Multicolor|0
figure.walk.treadmill.circle|SFFigureWalkTreadmillCircle|Draw,Fitness,Human,Variable|0
figure.walk.treadmill|SFFigureWalkTreadmill|Fitness,Human|0
figure.walk.triangle.fill|SFFigureWalkTriangleFill|Human,Maps,Multicolor,Transportation|0
figure.walk.triangle|SFFigureWalkTriangle|Human,Maps,Transportation|0
figure.water.fitness.circle.fill|SFFigureWaterFitnessCircleFill|Fitness,Human,Multicolor|0
figure.water.fitness.circle|SFFigureWaterFitnessCircle|Draw,Fitness,Human,Variable|0
figure.water.fitness|SFFigureWaterFitness|Fitness,Human|0
figure.waterpolo.circle.fill|SFFigureWaterpoloCircleFill|Fitness,Human,Multicolor|0
figure.waterpolo.circle|SFFigureWaterpoloCircle|Draw,Fitness,Human,Variable|0
figure.waterpolo|SFFigureWaterpolo|Fitness,Human|0
figure.wave.circle.fill|SFFigureWaveCircleFill|Human,Maps,Multicolor,Transportation|0
figure.wave.circle|SFFigureWaveCircle|Draw,Human,Maps,Transportation,Variable|0
figure.wave|SFFigureWave|Human,Maps,Transportation|0
figure.wrestling.circle.fill|SFFigureWrestlingCircleFill|Fitness,Human,Multicolor|0
figure.wrestling.circle|SFFigureWrestlingCircle|Draw,Fitness,Human,Variable|0
figure.wrestling|SFFigureWrestling|Fitness,Human|0
figure.yoga.circle.fill|SFFigureYogaCircleFill|Fitness,Human,Multicolor|0
figure.yoga.circle|SFFigureYogaCircle|Draw,Fitness,Human,Variable|0
figure.yoga|SFFigureYoga|Fitness,Human|0
filemenu.and.pointer.arrow|SFFilemenuAndPointerArrow|Accessibility|0
filemenu.and.selection|SFFilemenuAndSelection|Multicolor|0
film.circle.fill|SFFilmCircleFill|Multicolor,Objects & Tools|0
film.circle|SFFilmCircle|Draw,Objects & Tools,Variable|0
film.fill|SFFilmFill|Objects & Tools|0
film.stack.fill|SFFilmStackFill|Objects & Tools|0
film.stack|SFFilmStack|Objects & Tools|0
film|SFFilm|Objects & Tools|0
finder|SFFinder||1
fire.extinguisher.fill|SFFireExtinguisherFill|Objects & Tools|0
fire.extinguisher|SFFireExtinguisher|Objects & Tools|0
fireplace.fill|SFFireplaceFill|Home,Objects & Tools|0
fireplace|SFFireplace|Home,Objects & Tools|0
firewall.fill|SFFirewallFill|Multicolor,Privacy & Security|0
firewall|SFFirewall|Privacy & Security|0
fireworks|SFFireworks|Objects & Tools|0
fish.circle.fill|SFFishCircleFill|Multicolor,Nature|0
fish.circle|SFFishCircle|Draw,Nature,Variable|0
fish.fill|SFFishFill|Nature|0
fish|SFFish|Nature|0
flag.2.crossed.circle.fill|SFFlag2CrossedCircleFill|Fitness,Gaming,Multicolor,Objects & Tools|0
flag.2.crossed.circle|SFFlag2CrossedCircle|Draw,Fitness,Gaming,Objects & Tools,Variable|0
flag.2.crossed.fill|SFFlag2CrossedFill|Fitness,Gaming,Objects & Tools|0
flag.2.crossed|SFFlag2Crossed|Fitness,Gaming,Objects & Tools|0
flag.and.flag.filled.crossed|SFFlagAndFlagFilledCrossed|Fitness,Gaming,Objects & Tools|0
flag.badge.ellipsis.fill|SFFlagBadgeEllipsisFill|Multicolor,Objects & Tools|0
flag.badge.ellipsis|SFFlagBadgeEllipsis|Multicolor,Objects & Tools|0
flag.circle.fill|SFFlagCircleFill|Multicolor,Objects & Tools|0
flag.circle|SFFlagCircle|Draw,Multicolor,Objects & Tools,Variable|0
flag.fill|SFFlagFill|Multicolor,Objects & Tools|0
flag.filled.and.flag.crossed|SFFlagFilledAndFlagCrossed|Fitness,Gaming,Objects & Tools|0
flag.pattern.checkered.2.crossed|SFFlagPatternCheckered2Crossed|Fitness,Gaming,Objects & Tools|0
flag.pattern.checkered.circle.fill|SFFlagPatternCheckeredCircleFill|Fitness,Gaming,Multicolor,Objects & Tools|0
flag.pattern.checkered.circle|SFFlagPatternCheckeredCircle|Draw,Fitness,Gaming,Objects & Tools,Variable|0
flag.pattern.checkered.lc|SFFlagPatternCheckeredLc|Automotive,Objects & Tools|0
flag.pattern.checkered|SFFlagPatternCheckered|Fitness,Gaming,Objects & Tools|0
flag.slash.circle.fill|SFFlagSlashCircleFill|Multicolor,Objects & Tools|0
flag.slash.circle|SFFlagSlashCircle|Draw,Multicolor,Objects & Tools,Variable|0
flag.slash.fill|SFFlagSlashFill|Draw,Multicolor,Objects & Tools|0
flag.slash|SFFlagSlash|Draw,Multicolor,Objects & Tools|0
flag.square.fill|SFFlagSquareFill|Multicolor,Objects & Tools|0
flag.square|SFFlagSquare|Draw,Multicolor,Objects & Tools|0
flag|SFFlag|Multicolor,Objects & Tools|0
flame.circle.fill|SFFlameCircleFill|Multicolor,Nature|0
flame.circle|SFFlameCircle|Draw,Nature,Variable|0
flame.fill|SFFlameFill|Nature|0
flame.gauge.open|SFFlameGaugeOpen|Draw,Variable|0
flame|SFFlame|Nature|0
flashlight.off.circle.fill|SFFlashlightOffCircleFill|Multicolor,Objects & Tools|0
flashlight.off.circle|SFFlashlightOffCircle|Draw,Objects & Tools,Variable|0
flashlight.off.fill|SFFlashlightOffFill|Objects & Tools|0
flashlight.on.circle.fill|SFFlashlightOnCircleFill|Multicolor,Objects & Tools|0
flashlight.on.circle|SFFlashlightOnCircle|Draw,Objects & Tools,Variable|0
flashlight.on.fill|SFFlashlightOnFill|Objects & Tools|0
flashlight.slash.circle.fill|SFFlashlightSlashCircleFill|Multicolor,Objects & Tools|0
flashlight.slash.circle|SFFlashlightSlashCircle|Draw,Objects & Tools,Variable|0
flashlight.slash|SFFlashlightSlash|Draw,Objects & Tools|0
flask.fill|SFFlaskFill|Multicolor,Objects & Tools|0
flask|SFFlask|Multicolor,Objects & Tools|0
fleuron.fill|SFFleuronFill|Text Formatting|0
fleuron|SFFleuron|Text Formatting|0
flipphone|SFFlipphone|Devices|1
florinsign.arrow.trianglehead.counterclockwise.rotate.90|SFFlorinsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
florinsign.bank.building.fill|SFFlorinsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
florinsign.bank.building|SFFlorinsignBankBuilding|Commerce,Objects & Tools|0
florinsign.circle.fill|SFFlorinsignCircleFill|Commerce,Indices,Multicolor|0
florinsign.circle|SFFlorinsignCircle|Commerce,Draw,Indices,Variable|0
florinsign.gauge.chart.lefthalf.righthalf|SFFlorinsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
florinsign.gauge.chart.leftthird.topthird.rightthird|SFFlorinsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
florinsign.ring.dashed|SFFlorinsignRingDashed|Commerce,Home,Variable|0
florinsign.ring|SFFlorinsignRing|Commerce,Draw,Home|0
florinsign.square.fill|SFFlorinsignSquareFill|Commerce,Indices,Multicolor|0
florinsign.square|SFFlorinsignSquare|Commerce,Draw,Indices|0
florinsign|SFFlorinsign|Commerce,Indices|0
flowchart.fill|SFFlowchartFill||0
flowchart|SFFlowchart||0
fluid.batteryblock|SFFluidBatteryblock|Automotive,Objects & Tools|0
fluid.brakesignal|SFFluidBrakesignal|Automotive|0
fluid.coolant|SFFluidCoolant|Automotive,Objects & Tools|0
fluid.transmission|SFFluidTransmission|Automotive|0
fn|SFFn||0
folder.badge.gearshape|SFFolderBadgeGearshape|Objects & Tools|0
folder.badge.minus|SFFolderBadgeMinus|Multicolor,Objects & Tools|0
folder.badge.person.crop|SFFolderBadgePersonCrop|Human,Multicolor,Objects & Tools|0
folder.badge.plus|SFFolderBadgePlus|Multicolor,Objects & Tools|0
folder.badge.questionmark|SFFolderBadgeQuestionmark|Multicolor,Objects & Tools|0
folder.circle.fill|SFFolderCircleFill|Multicolor,Objects & Tools|0
folder.circle|SFFolderCircle|Draw,Multicolor,Objects & Tools,Variable|0
folder.fill.badge.gearshape|SFFolderFillBadgeGearshape|Objects & Tools|0
folder.fill.badge.minus|SFFolderFillBadgeMinus|Multicolor,Objects & Tools|0
folder.fill.badge.person.crop|SFFolderFillBadgePersonCrop|Human,Multicolor,Objects & Tools|0
folder.fill.badge.plus|SFFolderFillBadgePlus|Multicolor,Objects & Tools|0
folder.fill.badge.questionmark|SFFolderFillBadgeQuestionmark|Multicolor,Objects & Tools|0
folder.fill|SFFolderFill|Multicolor,Objects & Tools|0
folder|SFFolder|Multicolor,Objects & Tools|0
fork.knife.circle.fill|SFForkKnifeCircleFill|Multicolor,Objects & Tools|0
fork.knife.circle|SFForkKnifeCircle|Draw,Multicolor,Objects & Tools,Variable|0
fork.knife|SFForkKnife|Multicolor,Objects & Tools|0
formfitting.gamecontroller.fill|SFFormfittingGamecontrollerFill|Gaming|0
formfitting.gamecontroller|SFFormfittingGamecontroller|Gaming|0
forward.circle.fill|SFForwardCircleFill|Media,Multicolor|0
forward.circle|SFForwardCircle|Draw,Media,Variable|0
forward.end.alt.fill|SFForwardEndAltFill|Media|0
forward.end.alt|SFForwardEndAlt|Media|0
forward.end.circle.fill|SFForwardEndCircleFill|Media,Multicolor|0
forward.end.circle|SFForwardEndCircle|Draw,Media,Variable|0
forward.end.fill|SFForwardEndFill|Media|0
forward.end|SFForwardEnd|Media|0
forward.fill|SFForwardFill|Media|0
forward.frame.fill|SFForwardFrameFill|Media|0
forward.frame|SFForwardFrame|Media|0
forward|SFForward|Media|0
fossil.shell.fill|SFFossilShellFill|Nature,Objects & Tools|0
fossil.shell|SFFossilShell|Nature,Objects & Tools|0
francsign.arrow.trianglehead.counterclockwise.rotate.90|SFFrancsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
francsign.bank.building.fill|SFFrancsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
francsign.bank.building|SFFrancsignBankBuilding|Commerce,Objects & Tools|0
francsign.circle.fill|SFFrancsignCircleFill|Commerce,Indices,Multicolor|0
francsign.circle|SFFrancsignCircle|Commerce,Draw,Indices,Variable|0
francsign.gauge.chart.lefthalf.righthalf|SFFrancsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
francsign.gauge.chart.leftthird.topthird.rightthird|SFFrancsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
francsign.ring.dashed|SFFrancsignRingDashed|Commerce,Home,Variable|0
francsign.ring|SFFrancsignRing|Commerce,Draw,Home|0
francsign.square.fill|SFFrancsignSquareFill|Commerce,Indices,Multicolor|0
francsign.square|SFFrancsignSquare|Commerce,Draw,Indices|0
francsign|SFFrancsign|Commerce,Indices|0
frying.pan.fill|SFFryingPanFill|Home,Objects & Tools|0
frying.pan|SFFryingPan|Home,Objects & Tools|0
fuel.filter.water|SFFuelFilterWater|Automotive|0
fuelpump.and.filter|SFFuelpumpAndFilter|Automotive,Objects & Tools,Transportation|0
fuelpump.arrowtriangle.left.fill|SFFuelpumpArrowtriangleLeftFill|Automotive,Objects & Tools,Transportation|0
fuelpump.arrowtriangle.left|SFFuelpumpArrowtriangleLeft|Automotive,Objects & Tools,Transportation|0
fuelpump.arrowtriangle.right.fill|SFFuelpumpArrowtriangleRightFill|Automotive,Objects & Tools,Transportation|0
fuelpump.arrowtriangle.right|SFFuelpumpArrowtriangleRight|Automotive,Objects & Tools,Transportation|0
fuelpump.circle.fill|SFFuelpumpCircleFill|Automotive,Maps,Multicolor,Objects & Tools,Transportation|0
fuelpump.circle|SFFuelpumpCircle|Automotive,Draw,Maps,Objects & Tools,Transportation,Variable|0
fuelpump.exclamationmark.fill|SFFuelpumpExclamationmarkFill|Automotive,Objects & Tools,Transportation|0
fuelpump.exclamationmark|SFFuelpumpExclamationmark|Automotive,Objects & Tools,Transportation|0
fuelpump.fill|SFFuelpumpFill|Automotive,Maps,Objects & Tools,Transportation|0
fuelpump.slash.fill|SFFuelpumpSlashFill|Automotive,Draw,Objects & Tools,Transportation|0
fuelpump.slash|SFFuelpumpSlash|Automotive,Draw,Objects & Tools,Transportation|0
fuelpump|SFFuelpump|Automotive,Maps,Objects & Tools,Transportation|0
fuelpump.thermometer.fill|SFFuelpumpThermometerFill|Automotive,Objects & Tools,Transportation|0
fuelpump.thermometer|SFFuelpumpThermometer|Automotive,Objects & Tools,Transportation|0
function|SFFunction|Math|0
fx|SFFx||0
g.circle.fill|SFGCircleFill|Indices,Multicolor|0
g.circle|SFGCircle|Draw,Indices,Variable|0
g.square.fill|SFGSquareFill|Indices,Multicolor|0
g.square|SFGSquare|Draw,Indices|0
gamecontroller.circle.fill|SFGamecontrollerCircleFill|Devices,Fitness,Gaming,Multicolor,Objects & Tools|0
gamecontroller.circle|SFGamecontrollerCircle|Devices,Draw,Fitness,Gaming,Objects & Tools,Variable|0
gamecontroller.fill|SFGamecontrollerFill|Devices,Fitness,Gaming,Objects & Tools|0
gamecontroller|SFGamecontroller|Devices,Fitness,Gaming,Objects & Tools|0
gauge.chart.lefthalf.righthalf|SFGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
gauge.chart.leftthird.topthird.rightthird|SFGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
gauge.open.righthalf.dotted.with.needle.and.arrow.trianglehead.backward|SFGaugeOpenRighthalfDottedWithNeedleAndArrowTriangleheadBackward|Automotive,Draw|0
gauge.open|SFGaugeOpen|Draw,Variable|0
gauge.open.with.lines.needle.33percent.and.arrow.trianglehead.from.0percent.to.50percent|SFGaugeOpenWithLinesNeedle33percentAndArrowTriangleheadFrom0percentTo50percent|Automotive|0
gauge.open.with.lines.needle.33percent.and.arrowtriangle|SFGaugeOpenWithLinesNeedle33percentAndArrowtriangle|Automotive|0
gauge.open.with.lines.needle.33percent|SFGaugeOpenWithLinesNeedle33percent|Automotive|0
gauge.open.with.lines.needle.67percent.and.arrowtriangle.and.car|SFGaugeOpenWithLinesNeedle67percentAndArrowtriangleAndCar|Automotive|0
gauge.open.with.lines.needle.67percent.and.arrowtriangle|SFGaugeOpenWithLinesNeedle67percentAndArrowtriangle|Automotive|0
gauge.open.with.lines.needle.84percent.exclamation|SFGaugeOpenWithLinesNeedle84percentExclamation|Automotive|0
gauge.with.dots.needle.0percent|SFGaugeWithDotsNeedle0percent|Automotive,Objects & Tools|0
gauge.with.dots.needle.100percent|SFGaugeWithDotsNeedle100percent|Automotive,Objects & Tools|0
gauge.with.dots.needle.33percent|SFGaugeWithDotsNeedle33percent|Automotive,Objects & Tools|0
gauge.with.dots.needle.50percent|SFGaugeWithDotsNeedle50percent|Automotive,Objects & Tools|0
gauge.with.dots.needle.67percent|SFGaugeWithDotsNeedle67percent|Automotive,Objects & Tools|0
gauge.with.dots.needle.bottom.0percent|SFGaugeWithDotsNeedleBottom0percent|Automotive,Objects & Tools|0
gauge.with.dots.needle.bottom.100percent|SFGaugeWithDotsNeedleBottom100percent|Automotive,Objects & Tools|0
gauge.with.dots.needle.bottom.50percent.badge.minus|SFGaugeWithDotsNeedleBottom50percentBadgeMinus|Automotive,Multicolor,Objects & Tools|0
gauge.with.dots.needle.bottom.50percent.badge.plus|SFGaugeWithDotsNeedleBottom50percentBadgePlus|Automotive,Multicolor,Objects & Tools|0
gauge.with.dots.needle.bottom.50percent|SFGaugeWithDotsNeedleBottom50percent|Automotive,Objects & Tools|0
gauge.with.needle.fill|SFGaugeWithNeedleFill|Fitness,Objects & Tools,Time|0
gauge.with.needle|SFGaugeWithNeedle|Fitness,Objects & Tools,Time|0
gear.badge.checkmark|SFGearBadgeCheckmark|Multicolor|0
gear.badge.questionmark|SFGearBadgeQuestionmark|Multicolor|0
gear.badge|SFGearBadge|Multicolor|0
gear.badge.xmark|SFGearBadgeXmark|Multicolor|0
gear.circle.fill|SFGearCircleFill|Multicolor|0
gear.circle|SFGearCircle|Draw,Multicolor,Variable|0
gear|SFGear|Multicolor,Objects & Tools|0
gearshape.2.fill|SFGearshape2Fill|Objects & Tools|0
gearshape.2|SFGearshape2|Objects & Tools|0
gearshape.arrow.trianglehead.2.clockwise.rotate.90|SFGearshapeArrowTrianglehead2ClockwiseRotate90|Arrows,Draw|0
gearshape.circle.fill|SFGearshapeCircleFill|Multicolor|0
gearshape.circle|SFGearshapeCircle|Draw,Variable|0
gearshape.fill|SFGearshapeFill|Objects & Tools|0
gearshape|SFGearshape|Objects & Tools|0
gearshift.layout.sixspeed|SFGearshiftLayoutSixspeed|Gaming|0
gift.circle.fill|SFGiftCircleFill|Multicolor,Objects & Tools|0
gift.circle|SFGiftCircle|Draw,Objects & Tools,Variable|0
gift.fill|SFGiftFill|Multicolor,Objects & Tools|0
gift|SFGift|Multicolor,Objects & Tools|0
giftcard.fill|SFGiftcardFill|Commerce|0
giftcard|SFGiftcard|Commerce|0
globe.americas.fill|SFGlobeAmericasFill|Nature|0
globe.americas|SFGlobeAmericas|Nature|0
globe.asia.australia.fill|SFGlobeAsiaAustraliaFill|Nature|0
globe.asia.australia|SFGlobeAsiaAustralia|Nature|0
globe.badge.chevron.backward|SFGlobeBadgeChevronBackward|Keyboard,Multicolor|0
globe.badge.clock.fill|SFGlobeBadgeClockFill|Multicolor,Time|0
globe.badge.clock|SFGlobeBadgeClock|Multicolor,Time|0
globe.central.south.asia.fill|SFGlobeCentralSouthAsiaFill|Nature|0
globe.central.south.asia|SFGlobeCentralSouthAsia|Nature|0
globe.desk.fill|SFGlobeDeskFill|Objects & Tools|0
globe.desk|SFGlobeDesk|Objects & Tools|0
globe.europe.africa.fill|SFGlobeEuropeAfricaFill|Nature|0
globe.europe.africa|SFGlobeEuropeAfrica|Nature|0
globe.fill|SFGlobeFill|Keyboard|0
globe|SFGlobe|Draw,Keyboard|0
glowplug|SFGlowplug|Automotive,Draw|0
graduationcap.circle.fill|SFGraduationcapCircleFill|Multicolor,Objects & Tools|0
graduationcap.circle|SFGraduationcapCircle|Draw,Objects & Tools,Variable|0
graduationcap.fill|SFGraduationcapFill|Objects & Tools|0
graduationcap|SFGraduationcap|Objects & Tools|0
graph.2d|SFGraph2d|Draw,Math|0
graph.3d|SFGraph3d|Math|0
greaterthan.circle.fill|SFGreaterthanCircleFill|Math,Multicolor|0
greaterthan.circle|SFGreaterthanCircle|Draw,Math,Variable|0
greaterthan.square.fill|SFGreaterthanSquareFill|Math,Multicolor|0
greaterthan.square|SFGreaterthanSquare|Draw,Math|0
greaterthan|SFGreaterthan|Math|0
greaterthanorequalto.circle.fill|SFGreaterthanorequaltoCircleFill|Math,Multicolor|0
greaterthanorequalto.circle|SFGreaterthanorequaltoCircle|Draw,Math,Variable|0
greaterthanorequalto.square.fill|SFGreaterthanorequaltoSquareFill|Math,Multicolor|0
greaterthanorequalto.square|SFGreaterthanorequaltoSquare|Draw,Math|0
greaterthanorequalto|SFGreaterthanorequalto|Math|0
greetingcard.fill|SFGreetingcardFill|Objects & Tools|0
greetingcard|SFGreetingcard|Objects & Tools|0
grid.circle.fill|SFGridCircleFill|Multicolor|0
grid.circle|SFGridCircle|Draw,Variable|0
grid|SFGrid|Draw|0
guaranisign.arrow.trianglehead.counterclockwise.rotate.90|SFGuaranisignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
guaranisign.bank.building.fill|SFGuaranisignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
guaranisign.bank.building|SFGuaranisignBankBuilding|Commerce,Objects & Tools|0
guaranisign.circle.fill|SFGuaranisignCircleFill|Commerce,Indices,Multicolor|0
guaranisign.circle|SFGuaranisignCircle|Commerce,Draw,Indices,Variable|0
guaranisign.gauge.chart.lefthalf.righthalf|SFGuaranisignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
guaranisign.gauge.chart.leftthird.topthird.rightthird|SFGuaranisignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
guaranisign.ring.dashed|SFGuaranisignRingDashed|Commerce,Home,Variable|0
guaranisign.ring|SFGuaranisignRing|Commerce,Draw,Home|0
guaranisign.square.fill|SFGuaranisignSquareFill|Commerce,Indices,Multicolor|0
guaranisign.square|SFGuaranisignSquare|Commerce,Draw,Indices|0
guaranisign|SFGuaranisign|Commerce,Indices|0
guidepoint.horizontal|SFGuidepointHorizontal|Editing|0
guidepoint.vertical.arrowtriangle.forward|SFGuidepointVerticalArrowtriangleForward|Editing|0
guidepoint.vertical.numbers|SFGuidepointVerticalNumbers|Editing|0
guidepoint.vertical|SFGuidepointVertical|Editing|0
guitars.fill|SFGuitarsFill|Objects & Tools|0
guitars|SFGuitars|Objects & Tools|0
gyroscope|SFGyroscope|Objects & Tools|0
h.circle.fill|SFHCircleFill|Indices,Multicolor|0
h.circle|SFHCircle|Draw,Indices,Variable|0
h.square.fill|SFHSquareFill|Indices,Multicolor|0
h.square.on.square.fill|SFHSquareOnSquareFill||0
h.square.on.square|SFHSquareOnSquare||0
h.square|SFHSquare|Draw,Indices|0
hammer.circle.fill|SFHammerCircleFill|Multicolor,Objects & Tools|0
hammer.circle|SFHammerCircle|Draw,Objects & Tools,Variable|0
hammer.fill|SFHammerFill|Objects & Tools|0
hammer|SFHammer|Objects & Tools|0
hand.draw.badge.ellipsis.fill|SFHandDrawBadgeEllipsisFill|Human,Multicolor|0
hand.draw.badge.ellipsis|SFHandDrawBadgeEllipsis|Human,Multicolor|0
hand.draw.fill|SFHandDrawFill|Human|0
hand.draw|SFHandDraw|Human|0
hand.palm.facing.fill|SFHandPalmFacingFill|Human|0
hand.palm.facing|SFHandPalmFacing|Human|0
hand.pinch.fill|SFHandPinchFill|Human|0
hand.pinch|SFHandPinch|Human|0
hand.point.down.fill|SFHandPointDownFill|Human|0
hand.point.down|SFHandPointDown|Human|0
hand.point.left.fill|SFHandPointLeftFill|Human|0
hand.point.left|SFHandPointLeft|Human|0
hand.point.right.fill|SFHandPointRightFill|Human|0
hand.point.right|SFHandPointRight|Human|0
hand.point.up.braille.badge.ellipsis.fill|SFHandPointUpBrailleBadgeEllipsisFill|Accessibility,Human,Multicolor|0
hand.point.up.braille.badge.ellipsis|SFHandPointUpBrailleBadgeEllipsis|Accessibility,Human,Multicolor|0
hand.point.up.braille.fill|SFHandPointUpBrailleFill|Accessibility,Human|0
hand.point.up.braille|SFHandPointUpBraille|Accessibility,Human|0
hand.point.up.fill|SFHandPointUpFill|Accessibility,Human|0
hand.point.up.left.and.text.fill|SFHandPointUpLeftAndTextFill|Draw,Human|0
hand.point.up.left.and.text|SFHandPointUpLeftAndText|Draw,Human|0
hand.point.up.left.fill|SFHandPointUpLeftFill|Human|0
hand.point.up.left|SFHandPointUpLeft|Human|0
hand.point.up|SFHandPointUp|Accessibility,Human|0
hand.raised.app.fill|SFHandRaisedAppFill|Human,Multicolor,Privacy & Security|0
hand.raised.app|SFHandRaisedApp|Human,Privacy & Security|0
hand.raised.brakesignal.slash|SFHandRaisedBrakesignalSlash|Automotive|0
hand.raised.brakesignal|SFHandRaisedBrakesignal|Automotive|0
hand.raised.circle.fill|SFHandRaisedCircleFill|Human,Multicolor,Privacy & Security|0
hand.raised.circle|SFHandRaisedCircle|Draw,Human,Multicolor,Privacy & Security,Variable|0
hand.raised.fill|SFHandRaisedFill|Human,Multicolor,Privacy & Security|0
hand.raised.fingers.spread.fill|SFHandRaisedFingersSpreadFill|Human|0
hand.raised.fingers.spread|SFHandRaisedFingersSpread|Human|0
hand.raised.palm.facing.fill|SFHandRaisedPalmFacingFill|Human|0
hand.raised.palm.facing|SFHandRaisedPalmFacing|Human|0
hand.raised.slash.fill|SFHandRaisedSlashFill|Draw,Human,Privacy & Security|0
hand.raised.slash|SFHandRaisedSlash|Draw,Human,Multicolor,Privacy & Security|0
hand.raised.square.fill|SFHandRaisedSquareFill|Human,Multicolor,Privacy & Security|0
hand.raised.square.on.square.fill|SFHandRaisedSquareOnSquareFill|Privacy & Security|0
hand.raised.square.on.square|SFHandRaisedSquareOnSquare|Privacy & Security|0
hand.raised.square|SFHandRaisedSquare|Draw,Human,Multicolor,Privacy & Security|0
hand.raised|SFHandRaised|Human,Multicolor,Privacy & Security|0
hand.rays.fill|SFHandRaysFill|Accessibility,Draw,Human|0
hand.rays|SFHandRays|Accessibility,Draw,Human|0
hand.tap.fill|SFHandTapFill|Accessibility,Human|0
hand.tap|SFHandTap|Accessibility,Human|0
hand.thumbsdown.circle.fill|SFHandThumbsdownCircleFill|Human,Multicolor|0
hand.thumbsdown.circle|SFHandThumbsdownCircle|Draw,Human,Variable|0
hand.thumbsdown.fill|SFHandThumbsdownFill|Human|0
hand.thumbsdown.filled.hand.thumbsup|SFHandThumbsdownFilledHandThumbsup|Human|0
hand.thumbsdown.hand.thumbsup.fill|SFHandThumbsdownHandThumbsupFill|Human|0
hand.thumbsdown.hand.thumbsup.filled|SFHandThumbsdownHandThumbsupFilled|Human|0
hand.thumbsdown.hand.thumbsup|SFHandThumbsdownHandThumbsup|Human|0
hand.thumbsdown.slash.fill|SFHandThumbsdownSlashFill|Draw,Human|0
hand.thumbsdown.slash|SFHandThumbsdownSlash|Draw,Human|0
hand.thumbsdown|SFHandThumbsdown|Human|0
hand.thumbsup.circle.fill|SFHandThumbsupCircleFill|Human,Multicolor|0
hand.thumbsup.circle|SFHandThumbsupCircle|Draw,Human,Variable|0
hand.thumbsup.fill|SFHandThumbsupFill|Human|0
hand.thumbsup.slash.fill|SFHandThumbsupSlashFill|Draw,Human|0
hand.thumbsup.slash|SFHandThumbsupSlash|Draw,Human|0
hand.thumbsup|SFHandThumbsup|Human|0
hand.wave.fill|SFHandWaveFill|Human|0
hand.wave|SFHandWave|Human|0
handbag.circle.fill|SFHandbagCircleFill|Multicolor,Objects & Tools|0
handbag.circle|SFHandbagCircle|Draw,Objects & Tools,Variable|0
handbag.fill|SFHandbagFill|Objects & Tools|0
handbag.sensor.tag.radiowaves.left.and.right.fill|SFHandbagSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
handbag.sensor.tag.radiowaves.left.and.right|SFHandbagSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
handbag|SFHandbag|Objects & Tools|0
hands.and.sparkles.fill|SFHandsAndSparklesFill|Human|0
hands.and.sparkles|SFHandsAndSparkles|Human|0
hands.clap.fill|SFHandsClapFill|Human|0
hands.clap|SFHandsClap|Human|0
hanger|SFHanger|Objects & Tools|0
hare.circle.fill|SFHareCircleFill|Accessibility,Multicolor,Nature|0
hare.circle|SFHareCircle|Accessibility,Draw,Nature,Variable|0
hare.fill|SFHareFill|Accessibility,Nature|0
hare|SFHare|Accessibility,Nature|0
hat.cap.fill|SFHatCapFill|Objects & Tools|0
hat.cap|SFHatCap|Objects & Tools|0
hat.widebrim.fill|SFHatWidebrimFill|Objects & Tools|0
hat.widebrim|SFHatWidebrim|Objects & Tools|0
hazardsign.fill|SFHazardsignFill|Automotive,Multicolor|0
hazardsign|SFHazardsign|Automotive,Multicolor|0
head.profile.vision.pro.remove|SFHeadProfileVisionProRemove|Devices|0
headlight.daytime.fill|SFHeadlightDaytimeFill|Automotive|0
headlight.daytime|SFHeadlightDaytime|Automotive|0
headlight.fog.fill|SFHeadlightFogFill|Automotive,Multicolor|0
headlight.fog|SFHeadlightFog|Automotive,Multicolor|0
headlight.high.beam.fill|SFHeadlightHighBeamFill|Automotive,Multicolor|0
headlight.high.beam|SFHeadlightHighBeam|Automotive,Multicolor|0
headlight.low.beam.fill|SFHeadlightLowBeamFill|Automotive,Multicolor|0
headlight.low.beam|SFHeadlightLowBeam|Automotive,Multicolor|0
headphones.circle.fill|SFHeadphonesCircleFill|Devices,Multicolor,Objects & Tools|0
headphones.circle|SFHeadphonesCircle|Devices,Draw,Objects & Tools,Variable|0
headphones.dots|SFHeadphonesDots|Devices,Objects & Tools,Variable|0
headphones.over.ear|SFHeadphonesOverEar|Devices,Objects & Tools|0
headphones.sensor.tag.radiowaves.left.and.right.fill|SFHeadphonesSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
headphones.sensor.tag.radiowaves.left.and.right|SFHeadphonesSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
headphones.slash|SFHeadphonesSlash|Devices,Draw,Objects & Tools|0
headphones|SFHeadphones|Devices,Objects & Tools|0
headset.circle.fill|SFHeadsetCircleFill|Automotive,Devices,Multicolor,Objects & Tools|0
headset.circle|SFHeadsetCircle|Automotive,Devices,Draw,Objects & Tools,Variable|0
headset|SFHeadset|Automotive,Devices,Objects & Tools|0
hearingdevice.and.signal.meter.fill|SFHearingdeviceAndSignalMeterFill|Accessibility,Devices,Health,Human,Variable|0
hearingdevice.and.signal.meter|SFHearingdeviceAndSignalMeter|Accessibility,Devices,Health,Human,Variable|0
hearingdevice.ear.fill|SFHearingdeviceEarFill|Accessibility,Devices,Health,Human|0
hearingdevice.ear|SFHearingdeviceEar|Accessibility,Devices,Health,Human|0
heart.badge.bolt.fill|SFHeartBadgeBoltFill|Health|0
heart.badge.bolt.slash.fill|SFHeartBadgeBoltSlashFill|Health|0
heart.badge.bolt.slash|SFHeartBadgeBoltSlash|Health|0
heart.badge.bolt|SFHeartBadgeBolt|Health|0
heart.circle.fill|SFHeartCircleFill|Health,Multicolor|0
heart.circle|SFHeartCircle|Draw,Health,Multicolor,Variable|0
heart.fill|SFHeartFill|Health,Multicolor|0
heart.gauge.open|SFHeartGaugeOpen|Draw,Variable|0
heart.rectangle.fill|SFHeartRectangleFill|Multicolor|0
heart.rectangle|SFHeartRectangle|Draw,Multicolor|0
heart.slash.circle.fill|SFHeartSlashCircleFill|Multicolor|0
heart.slash.circle|SFHeartSlashCircle|Draw,Multicolor,Variable|0
heart.slash.fill|SFHeartSlashFill|Draw,Multicolor|0
heart.slash|SFHeartSlash|Draw,Multicolor,Variable|0
heart.square.fill|SFHeartSquareFill|Multicolor|0
heart.square|SFHeartSquare|Draw,Multicolor|0
heart|SFHeart|Draw,Health,Multicolor,Variable|0
heart.text.clipboard.fill|SFHeartTextClipboardFill|Health,Multicolor,Objects & Tools|0
heart.text.clipboard|SFHeartTextClipboard|Health,Multicolor,Objects & Tools|0
heart.text.square.fill|SFHeartTextSquareFill|Draw,Health,Multicolor|0
heart.text.square|SFHeartTextSquare|Draw,Health|0
heat.element.windshield|SFHeatElementWindshield|Automotive|0
heat.waves.and.fan|SFHeatWavesAndFan|Automotive|0
heat.waves.circle.fill|SFHeatWavesCircleFill|Arrows,Automotive,Multicolor|0
heat.waves.circle|SFHeatWavesCircle|Arrows,Automotive,Draw,Variable|0
heat.waves.gauge.open|SFHeatWavesGaugeOpen|Draw,Variable|0
heat.waves|SFHeatWaves|Arrows,Automotive,Draw|0
heater.vertical.fill|SFHeaterVerticalFill|Home,Objects & Tools|0
heater.vertical|SFHeaterVertical|Home,Objects & Tools|0
helm|SFHelm|Draw|0
helmet.fill|SFHelmetFill|Objects & Tools|0
helmet|SFHelmet|Objects & Tools|0
hexagon.bottomhalf.filled|SFHexagonBottomhalfFilled||0
hexagon.fill|SFHexagonFill|Shapes|0
hexagon.lefthalf.filled|SFHexagonLefthalfFilled||0
hexagon.righthalf.filled|SFHexagonRighthalfFilled||0
hexagon|SFHexagon|Shapes|0
hexagon.tophalf.filled|SFHexagonTophalfFilled||0
hifireceiver.fill|SFHifireceiverFill|Home,Objects & Tools|0
hifireceiver|SFHifireceiver|Home,Objects & Tools|0
hifispeaker.2.badge.minus.fill|SFHifispeaker2BadgeMinusFill|Devices,Multicolor|0
hifispeaker.2.badge.minus|SFHifispeaker2BadgeMinus|Devices,Multicolor|0
hifispeaker.2.badge.plus.fill|SFHifispeaker2BadgePlusFill|Devices,Multicolor|0
hifispeaker.2.badge.plus|SFHifispeaker2BadgePlus|Devices,Multicolor|0
hifispeaker.2.fill|SFHifispeaker2Fill|Devices|0
hifispeaker.2|SFHifispeaker2|Devices|0
hifispeaker.and.appletv.fill|SFHifispeakerAndAppletvFill|Devices|1
hifispeaker.and.appletv|SFHifispeakerAndAppletv|Devices|1
hifispeaker.and.homepod.badge.minus.fill|SFHifispeakerAndHomepodBadgeMinusFill|Devices,Multicolor|1
hifispeaker.and.homepod.badge.minus|SFHifispeakerAndHomepodBadgeMinus|Devices,Multicolor|1
hifispeaker.and.homepod.badge.plus.fill|SFHifispeakerAndHomepodBadgePlusFill|Devices,Multicolor|1
hifispeaker.and.homepod.badge.plus|SFHifispeakerAndHomepodBadgePlus|Devices,Multicolor|1
hifispeaker.and.homepod.fill|SFHifispeakerAndHomepodFill|Devices|1
hifispeaker.and.homepod.mini.badge.minus.fill|SFHifispeakerAndHomepodMiniBadgeMinusFill|Devices,Multicolor|1
hifispeaker.and.homepod.mini.badge.minus|SFHifispeakerAndHomepodMiniBadgeMinus|Devices,Multicolor|1
hifispeaker.and.homepod.mini.badge.plus.fill|SFHifispeakerAndHomepodMiniBadgePlusFill|Devices,Multicolor|1
hifispeaker.and.homepod.mini.badge.plus|SFHifispeakerAndHomepodMiniBadgePlus|Devices,Multicolor|1
hifispeaker.and.homepod.mini.fill|SFHifispeakerAndHomepodMiniFill|Devices|1
hifispeaker.and.homepod.mini|SFHifispeakerAndHomepodMini|Devices|1
hifispeaker.and.homepod|SFHifispeakerAndHomepod|Devices|1
hifispeaker.arrow.forward.fill|SFHifispeakerArrowForwardFill|Devices|0
hifispeaker.arrow.forward|SFHifispeakerArrowForward|Devices,Draw|0
hifispeaker.badge.minus.fill|SFHifispeakerBadgeMinusFill|Devices,Multicolor|0
hifispeaker.badge.minus|SFHifispeakerBadgeMinus|Devices,Multicolor|0
hifispeaker.badge.plus.fill|SFHifispeakerBadgePlusFill|Devices,Multicolor|0
hifispeaker.badge.plus|SFHifispeakerBadgePlus|Devices,Multicolor|0
hifispeaker.fill|SFHifispeakerFill|Devices|0
hifispeaker|SFHifispeaker|Devices|0
highlighter.badge.ellipsis|SFHighlighterBadgeEllipsis|Editing,Multicolor,Objects & Tools|0
highlighter|SFHighlighter|Draw,Editing,Objects & Tools|0
hockey.puck.circle.fill|SFHockeyPuckCircleFill|Fitness,Multicolor,Objects & Tools|0
hockey.puck.circle|SFHockeyPuckCircle|Draw,Fitness,Objects & Tools,Variable|0
hockey.puck.fill|SFHockeyPuckFill|Fitness,Objects & Tools|0
hockey.puck|SFHockeyPuck|Fitness,Objects & Tools|0
hold.brakesignal|SFHoldBrakesignal|Automotive|0
homepod.2.badge.minus.fill|SFHomepod2BadgeMinusFill|Devices,Multicolor|1
homepod.2.badge.minus|SFHomepod2BadgeMinus|Devices,Multicolor|1
homepod.2.badge.plus.fill|SFHomepod2BadgePlusFill|Devices,Multicolor|1
homepod.2.badge.plus|SFHomepod2BadgePlus|Devices,Multicolor|1
homepod.2.fill|SFHomepod2Fill|Devices|1
homepod.2|SFHomepod2|Devices|1
homepod.and.appletv.fill|SFHomepodAndAppletvFill|Devices|1
homepod.and.appletv|SFHomepodAndAppletv|Devices|1
homepod.and.homepod.mini.badge.minus.fill|SFHomepodAndHomepodMiniBadgeMinusFill|Devices,Multicolor|1
homepod.and.homepod.mini.badge.minus|SFHomepodAndHomepodMiniBadgeMinus|Devices,Multicolor|1
homepod.and.homepod.mini.badge.plus.fill|SFHomepodAndHomepodMiniBadgePlusFill|Devices,Multicolor|1
homepod.and.homepod.mini.badge.plus|SFHomepodAndHomepodMiniBadgePlus|Devices,Multicolor|1
homepod.and.homepod.mini.fill|SFHomepodAndHomepodMiniFill|Devices|1
homepod.and.homepod.mini|SFHomepodAndHomepodMini|Devices|1
homepod.arrow.forward.fill|SFHomepodArrowForwardFill|Devices|1
homepod.arrow.forward|SFHomepodArrowForward|Devices,Draw|1
homepod.badge.checkmark.fill|SFHomepodBadgeCheckmarkFill|Devices,Multicolor|1
homepod.badge.checkmark|SFHomepodBadgeCheckmark|Devices,Multicolor|1
homepod.badge.minus.fill|SFHomepodBadgeMinusFill|Devices,Multicolor|1
homepod.badge.minus|SFHomepodBadgeMinus|Devices,Multicolor|1
homepod.badge.plus.fill|SFHomepodBadgePlusFill|Devices,Multicolor|1
homepod.badge.plus|SFHomepodBadgePlus|Devices,Multicolor|1
homepod.fill|SFHomepodFill|Devices|1
homepod.mini.2.badge.minus.fill|SFHomepodMini2BadgeMinusFill|Devices,Multicolor|1
homepod.mini.2.badge.minus|SFHomepodMini2BadgeMinus|Devices,Multicolor|1
homepod.mini.2.badge.plus.fill|SFHomepodMini2BadgePlusFill|Devices,Multicolor|1
homepod.mini.2.badge.plus|SFHomepodMini2BadgePlus|Devices,Multicolor|1
homepod.mini.2.fill|SFHomepodMini2Fill|Devices|1
homepod.mini.2|SFHomepodMini2|Devices|1
homepod.mini.and.appletv.fill|SFHomepodMiniAndAppletvFill|Devices|1
homepod.mini.and.appletv|SFHomepodMiniAndAppletv|Devices|1
homepod.mini.arrow.forward.fill|SFHomepodMiniArrowForwardFill|Devices|1
homepod.mini.arrow.forward|SFHomepodMiniArrowForward|Devices,Draw|1
homepod.mini.badge.checkmark.fill|SFHomepodMiniBadgeCheckmarkFill|Devices,Multicolor|1
homepod.mini.badge.checkmark|SFHomepodMiniBadgeCheckmark|Devices,Multicolor|1
homepod.mini.badge.minus.fill|SFHomepodMiniBadgeMinusFill|Devices,Multicolor|1
homepod.mini.badge.minus|SFHomepodMiniBadgeMinus|Devices,Multicolor|1
homepod.mini.badge.plus.fill|SFHomepodMiniBadgePlusFill|Devices,Multicolor|1
homepod.mini.badge.plus|SFHomepodMiniBadgePlus|Devices,Multicolor|1
homepod.mini.fill|SFHomepodMiniFill|Devices|1
homepod.mini|SFHomepodMini|Devices|1
homepod|SFHomepod|Devices|1
horn.blast.fill|SFHornBlastFill|Automotive,Objects & Tools|0
horn.blast|SFHornBlast|Automotive,Objects & Tools|0
horn.fill|SFHornFill|Automotive,Objects & Tools|0
horn|SFHorn|Automotive,Objects & Tools|0
hourglass.badge.eye|SFHourglassBadgeEye|Multicolor,Objects & Tools,Time|0
hourglass.badge.lock|SFHourglassBadgeLock|Multicolor,Objects & Tools,Time|0
hourglass.badge.plus|SFHourglassBadgePlus|Multicolor,Objects & Tools,Time|0
hourglass.bottomhalf.filled|SFHourglassBottomhalfFilled|Multicolor,Objects & Tools,Time|0
hourglass.circle.fill|SFHourglassCircleFill|Multicolor,Objects & Tools,Time|0
hourglass.circle|SFHourglassCircle|Draw,Multicolor,Objects & Tools,Time,Variable|0
hourglass|SFHourglass|Multicolor,Objects & Tools,Time|0
hourglass.tophalf.filled|SFHourglassTophalfFilled|Multicolor,Objects & Tools,Time|0
house.and.flag.circle.fill|SFHouseAndFlagCircleFill|Multicolor,Objects & Tools|0
house.and.flag.circle|SFHouseAndFlagCircle|Draw,Objects & Tools,Variable|0
house.and.flag.fill|SFHouseAndFlagFill|Objects & Tools|0
house.and.flag|SFHouseAndFlag|Objects & Tools|0
house.badge.exclamationmark.fill|SFHouseBadgeExclamationmarkFill|Media,Multicolor|0
house.badge.exclamationmark|SFHouseBadgeExclamationmark|Media,Multicolor|0
house.badge.wifi.fill|SFHouseBadgeWifiFill|Home,Multicolor,Variable|0
house.badge.wifi|SFHouseBadgeWifi|Home,Multicolor,Variable|0
house.circle.fill|SFHouseCircleFill|Gaming,Home,Multicolor|0
house.circle|SFHouseCircle|Draw,Gaming,Home,Multicolor,Variable|0
house.fill|SFHouseFill|Gaming,Home,Multicolor|0
house.lodge.circle.fill|SFHouseLodgeCircleFill|Multicolor,Objects & Tools|0
house.lodge.circle|SFHouseLodgeCircle|Draw,Objects & Tools,Variable|0
house.lodge.fill|SFHouseLodgeFill|Objects & Tools|0
house.lodge|SFHouseLodge|Objects & Tools|0
house.slash.fill|SFHouseSlashFill|Draw,Gaming,Home|0
house.slash|SFHouseSlash|Draw,Gaming,Home|0
house|SFHouse|Gaming,Home,Multicolor|0
hryvniasign.arrow.trianglehead.counterclockwise.rotate.90|SFHryvniasignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
hryvniasign.bank.building.fill|SFHryvniasignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
hryvniasign.bank.building|SFHryvniasignBankBuilding|Commerce,Objects & Tools|0
hryvniasign.circle.fill|SFHryvniasignCircleFill|Commerce,Indices,Multicolor|0
hryvniasign.circle|SFHryvniasignCircle|Commerce,Draw,Indices,Variable|0
hryvniasign.gauge.chart.lefthalf.righthalf|SFHryvniasignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
hryvniasign.gauge.chart.leftthird.topthird.rightthird|SFHryvniasignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
hryvniasign.ring.dashed|SFHryvniasignRingDashed|Commerce,Home,Variable|0
hryvniasign.ring|SFHryvniasignRing|Commerce,Draw,Home|0
hryvniasign.square.fill|SFHryvniasignSquareFill|Commerce,Indices,Multicolor|0
hryvniasign.square|SFHryvniasignSquare|Commerce,Draw,Indices|0
hryvniasign|SFHryvniasign|Commerce,Indices|0
humidifier.and.droplets.fill|SFHumidifierAndDropletsFill|Home,Objects & Tools,Variable|0
humidifier.and.droplets|SFHumidifierAndDroplets|Home,Objects & Tools,Variable|0
humidifier.and.ellipsis.fill|SFHumidifierAndEllipsisFill|Home,Objects & Tools,Variable|0
humidifier.and.ellipsis|SFHumidifierAndEllipsis|Home,Objects & Tools,Variable|0
humidifier.fill|SFHumidifierFill|Home,Objects & Tools|0
humidifier|SFHumidifier|Home,Objects & Tools|0
humidity.fill|SFHumidityFill|Draw,Nature,Variable,Weather|0
humidity|SFHumidity|Draw,Nature,Variable,Weather|0
hurricane.circle.fill|SFHurricaneCircleFill|Multicolor,Nature,Weather|0
hurricane.circle|SFHurricaneCircle|Draw,Nature,Variable,Weather|0
hurricane|SFHurricane|Multicolor,Nature,Weather|0
hydrogen.circle.fill|SFHydrogenCircleFill|Automotive,Multicolor|0
hydrogen.circle|SFHydrogenCircle|Automotive,Draw,Variable|0
hydrogen.square.fill|SFHydrogenSquareFill|Automotive,Multicolor|0
hydrogen.square|SFHydrogenSquare|Automotive,Draw|0
hydrogen|SFHydrogen|Automotive|0
i.circle.fill|SFICircleFill|Indices,Multicolor|0
i.circle|SFICircle|Draw,Indices,Variable|0
i.square.fill|SFISquareFill|Indices,Multicolor|0
i.square|SFISquare|Draw,Indices|0
icloud.and.arrow.down.fill|SFIcloudAndArrowDownFill|Connectivity,Draw|1
icloud.and.arrow.down|SFIcloudAndArrowDown|Connectivity,Draw|1
icloud.and.arrow.up.fill|SFIcloudAndArrowUpFill|Connectivity|1
icloud.and.arrow.up|SFIcloudAndArrowUp|Connectivity,Draw|1
icloud.circle.fill|SFIcloudCircleFill|Connectivity,Multicolor|1
icloud.circle|SFIcloudCircle|Connectivity,Draw,Multicolor,Variable|1
icloud.dashed|SFIcloudDashed|Connectivity|1
icloud.fill|SFIcloudFill|Connectivity,Multicolor|1
icloud.slash.fill|SFIcloudSlashFill|Connectivity,Draw,Multicolor|1
icloud.slash|SFIcloudSlash|Connectivity,Draw,Multicolor|1
icloud.square.fill|SFIcloudSquareFill|Connectivity,Multicolor|1
icloud.square|SFIcloudSquare|Connectivity,Draw,Multicolor|1
icloud|SFIcloud|Connectivity,Multicolor|1
increase.indent|SFIncreaseIndent|Draw,Multicolor,Text Formatting|0
increase.quotelevel|SFIncreaseQuotelevel|Draw,Multicolor,Text Formatting|0
indianrupeesign.arrow.trianglehead.counterclockwise.rotate.90|SFIndianrupeesignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
indianrupeesign.bank.building.fill|SFIndianrupeesignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
indianrupeesign.bank.building|SFIndianrupeesignBankBuilding|Commerce,Objects & Tools|0
indianrupeesign.circle.fill|SFIndianrupeesignCircleFill|Commerce,Indices,Multicolor|0
indianrupeesign.circle|SFIndianrupeesignCircle|Commerce,Draw,Indices,Variable|0
indianrupeesign.gauge.chart.lefthalf.righthalf|SFIndianrupeesignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
indianrupeesign.gauge.chart.leftthird.topthird.rightthird|SFIndianrupeesignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
indianrupeesign.ring.dashed|SFIndianrupeesignRingDashed|Commerce,Home,Variable|0
indianrupeesign.ring|SFIndianrupeesignRing|Commerce,Draw,Home|0
indianrupeesign.square.fill|SFIndianrupeesignSquareFill|Commerce,Indices,Multicolor|0
indianrupeesign.square|SFIndianrupeesignSquare|Commerce,Draw,Indices|0
indianrupeesign|SFIndianrupeesign|Commerce,Indices|0
infinity.circle.fill|SFInfinityCircleFill|Media,Multicolor|0
infinity.circle|SFInfinityCircle|Draw,Media,Variable|0
infinity|SFInfinity|Media|0
info.bubble.fill|SFInfoBubbleFill|Communication,Multicolor|0
info.bubble|SFInfoBubble|Communication|0
info.circle.fill|SFInfoCircleFill|Multicolor|0
info.circle|SFInfoCircle|Draw,Multicolor,Variable|0
info.circle.text.page.fill|SFInfoCircleTextPageFill||0
info.circle.text.page|SFInfoCircleTextPage||0
info.square.fill|SFInfoSquareFill|Automotive,Multicolor|0
info.square|SFInfoSquare|Automotive,Draw,Multicolor|0
info|SFInfo|Multicolor|0
info.triangle.fill|SFInfoTriangleFill|Multicolor|0
info.triangle|SFInfoTriangle||0
info.windshield|SFInfoWindshield|Automotive|0
inhaler.fill|SFInhalerFill|Objects & Tools|0
inhaler|SFInhaler|Objects & Tools|0
inset.filled.applewatch.case|SFInsetFilledApplewatchCase|Devices|1
inset.filled.bottomhalf.rectangle.portrait|SFInsetFilledBottomhalfRectanglePortrait||0
inset.filled.bottomhalf.rectangle|SFInsetFilledBottomhalfRectangle|Draw|0
inset.filled.bottomhalf.tophalf.rectangle|SFInsetFilledBottomhalfTophalfRectangle|Draw|0
inset.filled.bottomleading.bottomtrailing.rectangle|SFInsetFilledBottomleadingBottomtrailingRectangle|Draw|0
inset.filled.bottomleading.rectangle.portrait|SFInsetFilledBottomleadingRectanglePortrait||0
inset.filled.bottomleading.rectangle|SFInsetFilledBottomleadingRectangle|Draw|0
inset.filled.bottomleft.bottomright.rectangle|SFInsetFilledBottomleftBottomrightRectangle|Draw|0
inset.filled.bottomleft.rectangle.portrait|SFInsetFilledBottomleftRectanglePortrait||0
inset.filled.bottomleft.rectangle|SFInsetFilledBottomleftRectangle|Draw|0
inset.filled.bottomright.rectangle.portrait|SFInsetFilledBottomrightRectanglePortrait||0
inset.filled.bottomright.rectangle|SFInsetFilledBottomrightRectangle|Draw|0
inset.filled.bottomthird.rectangle.portrait|SFInsetFilledBottomthirdRectanglePortrait||0
inset.filled.bottomthird.rectangle|SFInsetFilledBottomthirdRectangle|Draw|0
inset.filled.bottomthird.square|SFInsetFilledBottomthirdSquare|Draw|0
inset.filled.bottomtrailing.rectangle.portrait|SFInsetFilledBottomtrailingRectanglePortrait||0
inset.filled.bottomtrailing.rectangle|SFInsetFilledBottomtrailingRectangle|Draw|0
inset.filled.capsule.portrait|SFInsetFilledCapsulePortrait||0
inset.filled.capsule|SFInsetFilledCapsule||0
inset.filled.center.rectangle.badge.plus|SFInsetFilledCenterRectangleBadgePlus|Multicolor|0
inset.filled.center.rectangle.portrait|SFInsetFilledCenterRectanglePortrait||0
inset.filled.center.rectangle|SFInsetFilledCenterRectangle|Draw|0
inset.filled.circle.dashed|SFInsetFilledCircleDashed|Editing|0
inset.filled.circle.slash|SFInsetFilledCircleSlash|Variable|0
inset.filled.circle|SFInsetFilledCircle|Draw,Variable|0
inset.filled.diamond|SFInsetFilledDiamond||0
inset.filled.leadinghalf.arrow.leading.rectangle|SFInsetFilledLeadinghalfArrowLeadingRectangle|Draw|0
inset.filled.leadinghalf.rectangle.portrait|SFInsetFilledLeadinghalfRectanglePortrait||0
inset.filled.leadinghalf.rectangle|SFInsetFilledLeadinghalfRectangle|Draw|0
inset.filled.leadinghalf.toptrailing.bottomtrailing.rectangle|SFInsetFilledLeadinghalfToptrailingBottomtrailingRectangle|Draw|0
inset.filled.leadinghalf.trailinghalf.rectangle|SFInsetFilledLeadinghalfTrailinghalfRectangle|Draw|0
inset.filled.leadingthird.rectangle.portrait|SFInsetFilledLeadingthirdRectanglePortrait||0
inset.filled.leadingthird.rectangle|SFInsetFilledLeadingthirdRectangle|Draw|0
inset.filled.leadingthird.square|SFInsetFilledLeadingthirdSquare|Draw|0
inset.filled.lefthalf.arrow.left.rectangle|SFInsetFilledLefthalfArrowLeftRectangle|Draw|0
inset.filled.lefthalf.rectangle.portrait|SFInsetFilledLefthalfRectanglePortrait||0
inset.filled.lefthalf.rectangle|SFInsetFilledLefthalfRectangle|Draw|0
inset.filled.lefthalf.righthalf.rectangle|SFInsetFilledLefthalfRighthalfRectangle|Draw|0
inset.filled.lefthalf.topright.bottomright.rectangle|SFInsetFilledLefthalfToprightBottomrightRectangle|Draw|0
inset.filled.leftthird.middlethird.rightthird.rectangle|SFInsetFilledLeftthirdMiddlethirdRightthirdRectangle|Draw,Variable|0
inset.filled.leftthird.rectangle.portrait|SFInsetFilledLeftthirdRectanglePortrait||0
inset.filled.leftthird.rectangle|SFInsetFilledLeftthirdRectangle|Draw|0
inset.filled.leftthird.square|SFInsetFilledLeftthirdSquare|Draw|0
inset.filled.oval.portrait|SFInsetFilledOvalPortrait||0
inset.filled.oval|SFInsetFilledOval||0
inset.filled.pano|SFInsetFilledPano||0
inset.filled.rectangle.and.person.filled.circle.fill|SFInsetFilledRectangleAndPersonFilledCircleFill|Human,Multicolor|0
inset.filled.rectangle.and.person.filled.circle|SFInsetFilledRectangleAndPersonFilledCircle|Draw,Human,Variable|0
inset.filled.rectangle.and.person.filled.slash|SFInsetFilledRectangleAndPersonFilledSlash|Human|0
inset.filled.rectangle.and.person.filled|SFInsetFilledRectangleAndPersonFilled|Human|0
inset.filled.rectangle.and.pointer.arrow|SFInsetFilledRectangleAndPointerArrow|Human|0
inset.filled.rectangle.badge.record|SFInsetFilledRectangleBadgeRecord|Human|0
inset.filled.rectangle.on.rectangle|SFInsetFilledRectangleOnRectangle||0
inset.filled.rectangle.portrait|SFInsetFilledRectanglePortrait||0
inset.filled.rectangle|SFInsetFilledRectangle|Draw|0
inset.filled.righthalf.arrow.right.rectangle|SFInsetFilledRighthalfArrowRightRectangle|Draw|0
inset.filled.righthalf.lefthalf.rectangle|SFInsetFilledRighthalfLefthalfRectangle|Draw|0
inset.filled.righthalf.rectangle.portrait|SFInsetFilledRighthalfRectanglePortrait||0
inset.filled.righthalf.rectangle|SFInsetFilledRighthalfRectangle|Draw|0
inset.filled.rightthird.rectangle.portrait|SFInsetFilledRightthirdRectanglePortrait||0
inset.filled.rightthird.rectangle|SFInsetFilledRightthirdRectangle|Draw|0
inset.filled.rightthird.square|SFInsetFilledRightthirdSquare|Draw|0
inset.filled.square.dashed|SFInsetFilledSquareDashed|Editing|0
inset.filled.square|SFInsetFilledSquare|Draw|0
inset.filled.tophalf.bottomhalf.rectangle|SFInsetFilledTophalfBottomhalfRectangle|Draw|0
inset.filled.tophalf.bottomleft.bottomright.rectangle|SFInsetFilledTophalfBottomleftBottomrightRectangle|Draw|0
inset.filled.tophalf.rectangle.portrait|SFInsetFilledTophalfRectanglePortrait||0
inset.filled.tophalf.rectangle|SFInsetFilledTophalfRectangle|Draw|0
inset.filled.topleading.bottomleading.trailinghalf.rectangle|SFInsetFilledTopleadingBottomleadingTrailinghalfRectangle|Draw|0
inset.filled.topleading.rectangle.portrait|SFInsetFilledTopleadingRectanglePortrait||0
inset.filled.topleading.rectangle|SFInsetFilledTopleadingRectangle|Draw|0
inset.filled.topleft.bottomleft.righthalf.rectangle|SFInsetFilledTopleftBottomleftRighthalfRectangle|Draw|0
inset.filled.topleft.rectangle.portrait|SFInsetFilledTopleftRectanglePortrait||0
inset.filled.topleft.rectangle|SFInsetFilledTopleftRectangle|Draw|0
inset.filled.topleft.topright.bottomhalf.rectangle|SFInsetFilledTopleftToprightBottomhalfRectangle|Draw|0
inset.filled.topleft.topright.bottomleft.bottomright.rectangle|SFInsetFilledTopleftToprightBottomleftBottomrightRectangle|Draw|0
inset.filled.topright.rectangle.portrait|SFInsetFilledToprightRectanglePortrait||0
inset.filled.topright.rectangle|SFInsetFilledToprightRectangle|Draw|0
inset.filled.topthird.middlethird.bottomthird.rectangle|SFInsetFilledTopthirdMiddlethirdBottomthirdRectangle|Draw,Variable|0
inset.filled.topthird.rectangle.portrait|SFInsetFilledTopthirdRectanglePortrait||0
inset.filled.topthird.rectangle|SFInsetFilledTopthirdRectangle|Draw|0
inset.filled.topthird.square|SFInsetFilledTopthirdSquare|Draw|0
inset.filled.toptrailing.rectangle.portrait|SFInsetFilledToptrailingRectanglePortrait||0
inset.filled.toptrailing.rectangle|SFInsetFilledToptrailingRectangle|Draw|0
inset.filled.trailinghalf.arrow.trailing.rectangle|SFInsetFilledTrailinghalfArrowTrailingRectangle|Draw|0
inset.filled.trailinghalf.leadinghalf.rectangle|SFInsetFilledTrailinghalfLeadinghalfRectangle|Draw|0
inset.filled.trailinghalf.rectangle.portrait|SFInsetFilledTrailinghalfRectanglePortrait||0
inset.filled.trailinghalf.rectangle|SFInsetFilledTrailinghalfRectangle|Draw|0
inset.filled.trailingthird.rectangle.portrait|SFInsetFilledTrailingthirdRectanglePortrait||0
inset.filled.trailingthird.rectangle|SFInsetFilledTrailingthirdRectangle|Draw|0
inset.filled.trailingthird.square|SFInsetFilledTrailingthirdSquare|Draw|0
inset.filled.triangle|SFInsetFilledTriangle||0
inset.filled.tv|SFInsetFilledTv|Devices|0
internaldrive.fill|SFInternaldriveFill|Objects & Tools|0
internaldrive|SFInternaldrive|Objects & Tools|0
ipad.and.arrow.forward|SFIpadAndArrowForward|Devices,Draw|0
ipad.badge.checkmark|SFIpadBadgeCheckmark|Devices,Multicolor|1
ipad.badge.exclamationmark|SFIpadBadgeExclamationmark|Devices,Multicolor|1
ipad.badge.location|SFIpadBadgeLocation|Devices,Multicolor|1
ipad.badge.play|SFIpadBadgePlay|Devices,Multicolor|1
ipad.case.and.iphone.case|SFIpadCaseAndIphoneCase|Devices|1
ipad.case|SFIpadCase|Devices|1
ipad.gen1.badge.exclamationmark|SFIpadGen1BadgeExclamationmark|Devices,Multicolor|1
ipad.gen1.badge.location|SFIpadGen1BadgeLocation|Devices,Multicolor|1
ipad.gen1.badge.play|SFIpadGen1BadgePlay|Devices,Multicolor|1
ipad.gen1.crop.homebutton.circle|SFIpadGen1CropHomebuttonCircle|Devices|1
ipad.gen1.landscape.badge.exclamationmark|SFIpadGen1LandscapeBadgeExclamationmark|Devices,Multicolor|1
ipad.gen1.landscape.badge.location|SFIpadGen1LandscapeBadgeLocation|Devices,Multicolor|1
ipad.gen1.landscape.badge.play|SFIpadGen1LandscapeBadgePlay|Devices,Multicolor|1
ipad.gen1.landscape.slash|SFIpadGen1LandscapeSlash|Devices|1
ipad.gen1.landscape|SFIpadGen1Landscape|Devices|1
ipad.gen1.sizes|SFIpadGen1Sizes|Devices|1
ipad.gen1.slash|SFIpadGen1Slash|Devices|1
ipad.gen1|SFIpadGen1|Devices|1
ipad.gen2.badge.exclamationmark|SFIpadGen2BadgeExclamationmark|Devices,Multicolor|1
ipad.gen2.badge.location|SFIpadGen2BadgeLocation|Devices,Multicolor|1
ipad.gen2.badge.play|SFIpadGen2BadgePlay|Devices,Multicolor|1
ipad.gen2.landscape.badge.exclamationmark|SFIpadGen2LandscapeBadgeExclamationmark|Devices,Multicolor|1
ipad.gen2.landscape.badge.location|SFIpadGen2LandscapeBadgeLocation|Devices,Multicolor|1
ipad.gen2.landscape.badge.play|SFIpadGen2LandscapeBadgePlay|Devices,Multicolor|1
ipad.gen2.landscape.slash|SFIpadGen2LandscapeSlash|Devices|1
ipad.gen2.landscape|SFIpadGen2Landscape|Devices|1
ipad.gen2.sizes|SFIpadGen2Sizes|Devices|1
ipad.gen2.slash|SFIpadGen2Slash|Devices|1
ipad.gen2|SFIpadGen2|Devices|1
ipad.landscape.and.applewatch|SFIpadLandscapeAndApplewatch|Devices|1
ipad.landscape.and.iphone.slash|SFIpadLandscapeAndIphoneSlash|Devices|1
ipad.landscape.and.iphone|SFIpadLandscapeAndIphone|Devices|1
ipad.landscape.and.ipod|SFIpadLandscapeAndIpod|Devices|1
ipad.landscape.badge.exclamationmark|SFIpadLandscapeBadgeExclamationmark|Devices,Multicolor|1
ipad.landscape.badge.location|SFIpadLandscapeBadgeLocation|Devices,Multicolor|1
ipad.landscape.badge.play|SFIpadLandscapeBadgePlay|Devices,Multicolor|1
ipad.landscape|SFIpadLandscape|Devices|1
ipad.rear.camera|SFIpadRearCamera|Camera & Photos,Devices|0
ipad.sizes|SFIpadSizes|Devices|1
ipad|SFIpad|Devices|1
iphone.and.arrow.forward.inward|SFIphoneAndArrowForwardInward|Devices,Draw|0
iphone.and.arrow.forward.outward|SFIphoneAndArrowForwardOutward|Devices,Draw|0
iphone.and.arrow.left.and.arrow.right.inward|SFIphoneAndArrowLeftAndArrowRightInward|Devices|1
iphone.and.arrow.right.inward|SFIphoneAndArrowRightInward|Devices,Draw|0
iphone.and.arrow.right.outward|SFIphoneAndArrowRightOutward|Devices,Draw|0
iphone.and.ipod|SFIphoneAndIpod|Devices|1
iphone.and.vision.pro|SFIphoneAndVisionPro|Devices|1
iphone.app.switcher|SFIphoneAppSwitcher|Devices|1
iphone.badge.checkmark|SFIphoneBadgeCheckmark|Devices,Multicolor|1
iphone.badge.exclamationmark|SFIphoneBadgeExclamationmark|Devices,Multicolor|1
iphone.badge.location|SFIphoneBadgeLocation|Devices,Multicolor|1
iphone.badge.play|SFIphoneBadgePlay|Devices,Multicolor|1
iphone.case|SFIphoneCase|Devices|1
iphone.circle.fill|SFIphoneCircleFill|Devices,Multicolor|1
iphone.circle|SFIphoneCircle|Devices,Draw,Variable|1
iphone.crop.circle|SFIphoneCropCircle|Devices|1
iphone.dock.motorized.viewfinder|SFIphoneDockMotorizedViewfinder|Devices,Multicolor|1
iphone.gen1.and.arrow.left|SFIphoneGen1AndArrowLeft|Devices,Draw|1
iphone.gen1.badge.exclamationmark|SFIphoneGen1BadgeExclamationmark|Devices,Multicolor|1
iphone.gen1.badge.location|SFIphoneGen1BadgeLocation|Devices,Multicolor|1
iphone.gen1.badge.play|SFIphoneGen1BadgePlay|Devices,Multicolor|1
iphone.gen1.circle.fill|SFIphoneGen1CircleFill|Devices,Multicolor|1
iphone.gen1.circle|SFIphoneGen1Circle|Devices,Draw,Variable|1
iphone.gen1.crop.circle|SFIphoneGen1CropCircle|Devices|1
iphone.gen1.crop.homebutton.circle|SFIphoneGen1CropHomebuttonCircle|Devices|1
iphone.gen1.landscape.slash|SFIphoneGen1LandscapeSlash|Devices|1
iphone.gen1.landscape|SFIphoneGen1Landscape|Devices|1
iphone.gen1.motion|SFIphoneGen1Motion|Devices|1
iphone.gen1.radiowaves.left.and.right.circle.fill|SFIphoneGen1RadiowavesLeftAndRightCircleFill|Devices,Multicolor,Variable|1
iphone.gen1.radiowaves.left.and.right.circle|SFIphoneGen1RadiowavesLeftAndRightCircle|Devices,Draw,Variable|1
iphone.gen1.radiowaves.left.and.right|SFIphoneGen1RadiowavesLeftAndRight|Devices,Draw,Variable|1
iphone.gen1.sizes|SFIphoneGen1Sizes|Devices|1
iphone.gen1.slash.circle.fill|SFIphoneGen1SlashCircleFill|Devices,Multicolor|1
iphone.gen1.slash.circle|SFIphoneGen1SlashCircle|Devices,Draw,Variable|1
iphone.gen1.slash|SFIphoneGen1Slash|Devices|1
iphone.gen1|SFIphoneGen1|Devices|1
iphone.gen2.and.arrow.left.and.arrow.right.inward|SFIphoneGen2AndArrowLeftAndArrowRightInward|Devices,Draw|1
iphone.gen2.badge.exclamationmark|SFIphoneGen2BadgeExclamationmark|Devices,Multicolor|1
iphone.gen2.badge.location|SFIphoneGen2BadgeLocation|Devices,Multicolor|1
iphone.gen2.badge.play|SFIphoneGen2BadgePlay|Devices,Multicolor|1
iphone.gen2.circle.fill|SFIphoneGen2CircleFill|Devices,Multicolor|1
iphone.gen2.circle|SFIphoneGen2Circle|Devices,Draw,Variable|1
iphone.gen2.crop.circle|SFIphoneGen2CropCircle|Devices|1
iphone.gen2.landscape.slash|SFIphoneGen2LandscapeSlash|Devices|1
iphone.gen2.landscape|SFIphoneGen2Landscape|Devices|1
iphone.gen2.motion|SFIphoneGen2Motion|Devices|1
iphone.gen2.radiowaves.left.and.right.circle.fill|SFIphoneGen2RadiowavesLeftAndRightCircleFill|Devices,Multicolor,Variable|1
iphone.gen2.radiowaves.left.and.right.circle|SFIphoneGen2RadiowavesLeftAndRightCircle|Devices,Draw,Variable|1
iphone.gen2.radiowaves.left.and.right|SFIphoneGen2RadiowavesLeftAndRight|Devices,Draw,Variable|1
iphone.gen2.sizes|SFIphoneGen2Sizes|Devices|1
iphone.gen2.slash.circle.fill|SFIphoneGen2SlashCircleFill|Devices,Multicolor|1
iphone.gen2.slash.circle|SFIphoneGen2SlashCircle|Devices,Draw,Variable|1
iphone.gen2.slash|SFIphoneGen2Slash|Devices|1
iphone.gen2|SFIphoneGen2|Devices|1
iphone.gen3.and.arrow.left.and.arrow.right.inward|SFIphoneGen3AndArrowLeftAndArrowRightInward|Devices,Draw|1
iphone.gen3.badge.exclamationmark|SFIphoneGen3BadgeExclamationmark|Devices,Multicolor|1
iphone.gen3.badge.location|SFIphoneGen3BadgeLocation|Devices,Multicolor|1
iphone.gen3.badge.play|SFIphoneGen3BadgePlay|Devices,Multicolor|1
iphone.gen3.circle.fill|SFIphoneGen3CircleFill|Devices,Multicolor|1
iphone.gen3.circle|SFIphoneGen3Circle|Devices,Draw,Variable|1
iphone.gen3.crop.circle|SFIphoneGen3CropCircle|Devices|1
iphone.gen3.landscape.slash|SFIphoneGen3LandscapeSlash|Devices|1
iphone.gen3.landscape|SFIphoneGen3Landscape|Devices|1
iphone.gen3.motion|SFIphoneGen3Motion|Devices|1
iphone.gen3.radiowaves.left.and.right.circle.fill|SFIphoneGen3RadiowavesLeftAndRightCircleFill|Devices,Multicolor,Variable|1
iphone.gen3.radiowaves.left.and.right.circle|SFIphoneGen3RadiowavesLeftAndRightCircle|Devices,Draw,Variable|1
iphone.gen3.radiowaves.left.and.right|SFIphoneGen3RadiowavesLeftAndRight|Devices,Draw,Variable|1
iphone.gen3.sizes|SFIphoneGen3Sizes|Devices|1
iphone.gen3.slash.circle.fill|SFIphoneGen3SlashCircleFill|Devices,Multicolor|1
iphone.gen3.slash.circle|SFIphoneGen3SlashCircle|Devices,Draw,Variable|1
iphone.gen3.slash|SFIphoneGen3Slash|Devices|1
iphone.gen3|SFIphoneGen3|Devices|1
iphone.landscape|SFIphoneLandscape|Devices|1
iphone.motion|SFIphoneMotion|Devices|1
iphone.pattern.diagonalline.on.rectangle.portrait.dashed|SFIphonePatternDiagonallineOnRectanglePortraitDashed|Devices|0
iphone.pattern.diagonalline|SFIphonePatternDiagonalline|Devices|0
iphone.radiowaves.left.and.right.circle.fill|SFIphoneRadiowavesLeftAndRightCircleFill|Devices,Multicolor,Variable|1
iphone.radiowaves.left.and.right.circle|SFIphoneRadiowavesLeftAndRightCircle|Devices,Draw,Variable|1
iphone.radiowaves.left.and.right|SFIphoneRadiowavesLeftAndRight|Devices,Draw,Variable|1
iphone.rear.camera|SFIphoneRearCamera|Camera & Photos,Devices|0
iphone.sizes|SFIphoneSizes|Devices|1
iphone.slash.circle.fill|SFIphoneSlashCircleFill|Devices,Multicolor|1
iphone.slash.circle|SFIphoneSlashCircle|Devices,Draw,Variable|1
iphone.slash|SFIphoneSlash|Devices|1
iphone.smartbatterycase.gen1|SFIphoneSmartbatterycaseGen1|Devices|1
iphone.smartbatterycase.gen2|SFIphoneSmartbatterycaseGen2|Devices|1
iphone|SFIphone|Devices|1
ipod.and.applewatch|SFIpodAndApplewatch|Devices|1
ipod.and.vision.pro|SFIpodAndVisionPro|Devices|1
ipod.shuffle.gen1|SFIpodShuffleGen1|Devices|1
ipod.shuffle.gen2|SFIpodShuffleGen2|Devices|1
ipod.shuffle.gen3|SFIpodShuffleGen3|Devices|1
ipod.shuffle.gen4|SFIpodShuffleGen4|Devices|1
ipod|SFIpod|Devices|1
ipod.touch.landscape|SFIpodTouchLandscape|Devices|1
ipod.touch.slash|SFIpodTouchSlash|Devices|1
ipod.touch|SFIpodTouch|Devices|1
italic|SFItalic|Multicolor,Text Formatting|0
ivfluid.bag.fill|SFIvfluidBagFill|Health,Multicolor,Objects & Tools|0
ivfluid.bag|SFIvfluidBag|Health,Multicolor,Objects & Tools|0
j.circle.fill|SFJCircleFill|Indices,Multicolor|0
j.circle|SFJCircle|Draw,Indices,Variable|0
j.square.fill|SFJSquareFill|Indices,Multicolor|0
j.square.on.square.fill|SFJSquareOnSquareFill||0
j.square.on.square|SFJSquareOnSquare||0
j.square|SFJSquare|Draw,Indices|0
jacket.circle.fill|SFJacketCircleFill|Multicolor|0
jacket.circle|SFJacketCircle|Draw,Variable|0
jacket.fill|SFJacketFill|Objects & Tools|0
jacket.sensor.tag.radiowaves.left.and.right.fill|SFJacketSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
jacket.sensor.tag.radiowaves.left.and.right|SFJacketSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
jacket|SFJacket|Objects & Tools|0
k.circle.fill|SFKCircleFill|Indices,Multicolor|0
k.circle|SFKCircle|Draw,Indices,Variable|0
k.square.fill|SFKSquareFill|Indices,Multicolor|0
k.square|SFKSquare|Draw,Indices|0
k|SFK||0
kashida.arabic|SFKashidaArabic|Draw,Text Formatting|0
key.2.on.ring.fill|SFKey2OnRingFill|Objects & Tools|0
key.2.on.ring|SFKey2OnRing|Objects & Tools|0
key.car.radiowaves.forward.fill|SFKeyCarRadiowavesForwardFill|Automotive,Draw,Objects & Tools,Variable|0
key.car.radiowaves.forward|SFKeyCarRadiowavesForward|Automotive,Draw,Objects & Tools,Variable|0
key.car.side.fill|SFKeyCarSideFill|Automotive,Multicolor|0
key.car.side|SFKeyCarSide|Automotive|0
key.card.fill|SFKeyCardFill|Automotive,Multicolor,Objects & Tools|0
key.card|SFKeyCard|Automotive,Objects & Tools|0
key.circle.fill|SFKeyCircleFill|Automotive,Multicolor,Objects & Tools,Privacy & Security|0
key.circle|SFKeyCircle|Automotive,Draw,Objects & Tools,Privacy & Security,Variable|0
key.convertible.side.fill|SFKeyConvertibleSideFill|Automotive,Multicolor|0
key.convertible.side|SFKeyConvertibleSide|Automotive|0
key.fill|SFKeyFill|Automotive,Objects & Tools,Privacy & Security|0
key.horizontal.fill|SFKeyHorizontalFill|Automotive,Objects & Tools,Privacy & Security|0
key.horizontal|SFKeyHorizontal|Automotive,Objects & Tools,Privacy & Security|0
key.icloud.fill|SFKeyIcloudFill|Connectivity,Multicolor,Privacy & Security|1
key.icloud|SFKeyIcloud|Connectivity,Privacy & Security|1
key.radiowaves.forward.fill|SFKeyRadiowavesForwardFill|Automotive,Draw,Objects & Tools,Privacy & Security,Variable|0
key.radiowaves.forward.slash.fill|SFKeyRadiowavesForwardSlashFill|Automotive,Objects & Tools,Privacy & Security|0
key.radiowaves.forward.slash|SFKeyRadiowavesForwardSlash|Automotive,Objects & Tools,Privacy & Security|0
key.radiowaves.forward|SFKeyRadiowavesForward|Automotive,Draw,Objects & Tools,Privacy & Security,Variable|0
key.sensor.tag.radiowaves.left.and.right.fill|SFKeySensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
key.sensor.tag.radiowaves.left.and.right|SFKeySensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
key.shield.fill|SFKeyShieldFill|Multicolor,Privacy & Security|0
key.shield|SFKeyShield|Privacy & Security|0
key.slash.fill|SFKeySlashFill|Automotive,Draw,Objects & Tools,Privacy & Security|0
key.slash|SFKeySlash|Automotive,Draw,Objects & Tools,Privacy & Security|0
key.suv.side.fill|SFKeySuvSideFill|Automotive,Multicolor|0
key.suv.side|SFKeySuvSide|Automotive|0
key|SFKey|Automotive,Objects & Tools,Privacy & Security|0
key.truck.pickup.side.fill|SFKeyTruckPickupSideFill|Automotive,Multicolor|0
key.truck.pickup.side|SFKeyTruckPickupSide|Automotive|0
key.viewfinder|SFKeyViewfinder|Objects & Tools,Privacy & Security|0
keyboard.badge.ellipsis.fill|SFKeyboardBadgeEllipsisFill|Devices,Keyboard,Multicolor|0
keyboard.badge.ellipsis|SFKeyboardBadgeEllipsis|Devices,Keyboard,Multicolor|0
keyboard.badge.eye.fill|SFKeyboardBadgeEyeFill|Devices,Keyboard|0
keyboard.badge.eye|SFKeyboardBadgeEye|Devices,Keyboard|0
keyboard.chevron.compact.down.fill|SFKeyboardChevronCompactDownFill|Devices,Keyboard|0
keyboard.chevron.compact.down|SFKeyboardChevronCompactDown|Devices,Keyboard|0
keyboard.chevron.compact.left.fill|SFKeyboardChevronCompactLeftFill|Devices,Keyboard|0
keyboard.chevron.compact.left|SFKeyboardChevronCompactLeft|Devices,Keyboard|0
keyboard.fill|SFKeyboardFill|Devices,Keyboard|0
keyboard.macwindow|SFKeyboardMacwindow|Multicolor|0
keyboard.onehanded.left.fill|SFKeyboardOnehandedLeftFill|Devices,Keyboard|0
keyboard.onehanded.left|SFKeyboardOnehandedLeft|Devices,Keyboard|0
keyboard.onehanded.right.fill|SFKeyboardOnehandedRightFill|Devices,Keyboard|0
keyboard.onehanded.right|SFKeyboardOnehandedRight|Devices,Keyboard|0
keyboard|SFKeyboard|Devices,Keyboard|0
kipsign.arrow.trianglehead.counterclockwise.rotate.90|SFKipsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
kipsign.bank.building.fill|SFKipsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
kipsign.bank.building|SFKipsignBankBuilding|Commerce,Objects & Tools|0
kipsign.circle.fill|SFKipsignCircleFill|Commerce,Indices,Multicolor|0
kipsign.circle|SFKipsignCircle|Commerce,Draw,Indices,Variable|0
kipsign.gauge.chart.lefthalf.righthalf|SFKipsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
kipsign.gauge.chart.leftthird.topthird.rightthird|SFKipsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
kipsign.ring.dashed|SFKipsignRingDashed|Commerce,Home,Variable|0
kipsign.ring|SFKipsignRing|Commerce,Draw,Home|0
kipsign.square.fill|SFKipsignSquareFill|Commerce,Indices,Multicolor|0
kipsign.square|SFKipsignSquare|Commerce,Draw,Indices|0
kipsign|SFKipsign|Commerce,Indices|0
kph.circle.fill|SFKphCircleFill|Automotive,Multicolor|0
kph.circle|SFKphCircle|Automotive,Draw,Variable|0
kph|SFKph|Automotive|0
l.button.roundedbottom.horizontal.fill|SFLButtonRoundedbottomHorizontalFill|Gaming,Multicolor|0
l.button.roundedbottom.horizontal|SFLButtonRoundedbottomHorizontal|Gaming|0
l.circle.fill|SFLCircleFill|Gaming,Indices,Multicolor|0
l.circle|SFLCircle|Draw,Gaming,Indices,Variable|0
l.joystick.fill|SFLJoystickFill|Gaming|0
l.joystick.press.down.fill|SFLJoystickPressDownFill|Gaming|0
l.joystick.press.down|SFLJoystickPressDown|Gaming|0
l.joystick|SFLJoystick|Gaming|0
l.joystick.tilt.down.fill|SFLJoystickTiltDownFill|Gaming|0
l.joystick.tilt.down|SFLJoystickTiltDown|Gaming|0
l.joystick.tilt.left.fill|SFLJoystickTiltLeftFill|Gaming|0
l.joystick.tilt.left|SFLJoystickTiltLeft|Gaming|0
l.joystick.tilt.right.fill|SFLJoystickTiltRightFill|Gaming|0
l.joystick.tilt.right|SFLJoystickTiltRight|Gaming|0
l.joystick.tilt.up.fill|SFLJoystickTiltUpFill|Gaming|0
l.joystick.tilt.up|SFLJoystickTiltUp|Gaming|0
l.square.fill|SFLSquareFill|Indices,Multicolor|0
l.square|SFLSquare|Draw,Indices|0
l1.button.roundedbottom.horizontal.fill|SFL1ButtonRoundedbottomHorizontalFill|Gaming,Multicolor|0
l1.button.roundedbottom.horizontal|SFL1ButtonRoundedbottomHorizontal|Gaming|0
l1.circle.fill|SFL1CircleFill|Gaming,Multicolor|0
l1.circle|SFL1Circle|Draw,Gaming,Variable|0
l2.button.angledtop.vertical.left.fill|SFL2ButtonAngledtopVerticalLeftFill|Gaming,Multicolor|0
l2.button.angledtop.vertical.left|SFL2ButtonAngledtopVerticalLeft|Gaming|0
l2.button.roundedtop.horizontal.fill|SFL2ButtonRoundedtopHorizontalFill|Gaming,Multicolor|0
l2.button.roundedtop.horizontal|SFL2ButtonRoundedtopHorizontal|Gaming|0
l2.circle.fill|SFL2CircleFill|Gaming,Multicolor|0
l2.circle|SFL2Circle|Draw,Gaming,Variable|0
l3.button.angledbottom.horizontal.left.fill|SFL3ButtonAngledbottomHorizontalLeftFill|Gaming,Multicolor|0
l3.button.angledbottom.horizontal.left|SFL3ButtonAngledbottomHorizontalLeft|Gaming|0
l4.button.horizontal.fill|SFL4ButtonHorizontalFill|Gaming,Multicolor|0
l4.button.horizontal|SFL4ButtonHorizontal|Gaming|0
ladybug.circle.fill|SFLadybugCircleFill|Multicolor,Nature|0
ladybug.circle|SFLadybugCircle|Draw,Nature,Variable|0
ladybug.fill|SFLadybugFill|Multicolor,Nature|0
ladybug.slash.circle.fill|SFLadybugSlashCircleFill|Multicolor,Nature|0
ladybug.slash.circle|SFLadybugSlashCircle|Draw,Nature,Variable|0
ladybug.slash.fill|SFLadybugSlashFill|Nature|0
ladybug.slash|SFLadybugSlash|Draw,Nature|0
ladybug|SFLadybug|Nature|0
lamp.ceiling.fill|SFLampCeilingFill|Home,Objects & Tools|0
lamp.ceiling.inverse|SFLampCeilingInverse|Home,Objects & Tools|0
lamp.ceiling|SFLampCeiling|Home,Objects & Tools|0
lamp.desk.fill|SFLampDeskFill|Home,Objects & Tools|0
lamp.desk|SFLampDesk|Home,Objects & Tools|0
lamp.floor.fill|SFLampFloorFill|Home,Objects & Tools|0
lamp.floor|SFLampFloor|Home,Objects & Tools|0
lamp.table.fill|SFLampTableFill|Home,Objects & Tools|0
lamp.table|SFLampTable|Home,Objects & Tools|0
lane|SFLane|Fitness|0
lanyardcard.fill|SFLanyardcardFill|Objects & Tools|0
lanyardcard|SFLanyardcard|Objects & Tools|0
laptopcomputer.and.arrow.down|SFLaptopcomputerAndArrowDown|Devices,Draw|0
laptopcomputer.badge.checkmark|SFLaptopcomputerBadgeCheckmark|Devices,Multicolor|0
laptopcomputer.slash|SFLaptopcomputerSlash|Devices|0
laptopcomputer|SFLaptopcomputer|Devices|0
laptopcomputer.trianglebadge.exclamationmark|SFLaptopcomputerTrianglebadgeExclamationmark|Devices,Multicolor|0
larisign.arrow.trianglehead.counterclockwise.rotate.90|SFLarisignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
larisign.bank.building.fill|SFLarisignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
larisign.bank.building|SFLarisignBankBuilding|Commerce,Objects & Tools|0
larisign.circle.fill|SFLarisignCircleFill|Commerce,Indices,Multicolor|0
larisign.circle|SFLarisignCircle|Commerce,Draw,Indices,Variable|0
larisign.gauge.chart.lefthalf.righthalf|SFLarisignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
larisign.gauge.chart.leftthird.topthird.rightthird|SFLarisignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
larisign.ring.dashed|SFLarisignRingDashed|Commerce,Home,Variable|0
larisign.ring|SFLarisignRing|Commerce,Draw,Home|0
larisign.square.fill|SFLarisignSquareFill|Commerce,Indices,Multicolor|0
larisign.square|SFLarisignSquare|Commerce,Draw,Indices|0
larisign|SFLarisign|Commerce,Indices|0
laser.burst|SFLaserBurst|Draw,Objects & Tools|0
lasso.badge.sparkles|SFLassoBadgeSparkles|Editing,Objects & Tools|0
lasso|SFLasso|Draw,Editing,Objects & Tools|0
latch.2.case.fill|SFLatch2CaseFill|Objects & Tools|0
latch.2.case|SFLatch2Case|Objects & Tools|0
laurel.leading.laurel.trailing|SFLaurelLeadingLaurelTrailing||0
laurel.leading|SFLaurelLeading|Nature|0
laurel.trailing|SFLaurelTrailing|Nature|0
lb.button.roundedbottom.horizontal.fill|SFLbButtonRoundedbottomHorizontalFill|Gaming,Multicolor|0
lb.button.roundedbottom.horizontal|SFLbButtonRoundedbottomHorizontal|Gaming|0
lb.circle.fill|SFLbCircleFill|Gaming,Multicolor|0
lb.circle|SFLbCircle|Draw,Gaming,Variable|0
leaf.arrow.trianglehead.clockwise|SFLeafArrowTriangleheadClockwise|Arrows,Draw,Multicolor,Nature|0
leaf.circle.fill|SFLeafCircleFill|Multicolor,Nature|0
leaf.circle|SFLeafCircle|Draw,Multicolor,Nature,Variable|0
leaf.fill|SFLeafFill|Multicolor,Nature|0
leaf|SFLeaf|Multicolor,Nature|0
left.circle.fill|SFLeftCircleFill|Multicolor|0
left.circle|SFLeftCircle|Draw,Variable|0
left|SFLeft||0
lessthan.circle.fill|SFLessthanCircleFill|Math,Multicolor|0
lessthan.circle|SFLessthanCircle|Draw,Math,Variable|0
lessthan.square.fill|SFLessthanSquareFill|Math,Multicolor|0
lessthan.square|SFLessthanSquare|Draw,Math|0
lessthan|SFLessthan|Math|0
lessthanorequalto.circle.fill|SFLessthanorequaltoCircleFill|Math,Multicolor|0
lessthanorequalto.circle|SFLessthanorequaltoCircle|Draw,Math,Variable|0
lessthanorequalto.square.fill|SFLessthanorequaltoSquareFill|Math,Multicolor|0
lessthanorequalto.square|SFLessthanorequaltoSquare|Draw,Math|0
lessthanorequalto|SFLessthanorequalto|Math|0
level.fill|SFLevelFill|Objects & Tools|0
level|SFLevel|Objects & Tools|0
licenseplate.fill|SFLicenseplateFill|Automotive,Maps,Multicolor,Objects & Tools|0
licenseplate|SFLicenseplate|Automotive,Maps,Objects & Tools|0
lifepreserver.fill|SFLifepreserverFill|Objects & Tools|0
lifepreserver|SFLifepreserver|Objects & Tools|0
light.beacon.max.fill|SFLightBeaconMaxFill|Draw,Home,Multicolor,Objects & Tools|0
light.beacon.max|SFLightBeaconMax|Draw,Home,Multicolor,Objects & Tools|0
light.beacon.min.fill|SFLightBeaconMinFill|Home,Multicolor,Objects & Tools|0
light.beacon.min|SFLightBeaconMin|Home,Multicolor,Objects & Tools|0
light.cylindrical.ceiling.fill|SFLightCylindricalCeilingFill|Home,Objects & Tools|0
light.cylindrical.ceiling.inverse|SFLightCylindricalCeilingInverse|Home,Objects & Tools|0
light.cylindrical.ceiling|SFLightCylindricalCeiling|Home,Objects & Tools|0
light.max|SFLightMax|Draw,Keyboard|0
light.min|SFLightMin|Draw,Keyboard|0
light.overhead.left.fill|SFLightOverheadLeftFill|Automotive,Draw|0
light.overhead.left|SFLightOverheadLeft|Automotive,Draw|0
light.overhead.right.fill|SFLightOverheadRightFill|Automotive,Draw|0
light.overhead.right|SFLightOverheadRight|Automotive,Draw|0
light.panel.fill|SFLightPanelFill|Home,Objects & Tools|0
light.panel|SFLightPanel|Home,Objects & Tools|0
light.recessed.3.fill|SFLightRecessed3Fill|Home,Objects & Tools|0
light.recessed.3.inverse|SFLightRecessed3Inverse|Home,Objects & Tools|0
light.recessed.3|SFLightRecessed3|Home,Objects & Tools|0
light.recessed.fill|SFLightRecessedFill|Home,Objects & Tools|0
light.recessed.inverse|SFLightRecessedInverse|Home,Objects & Tools|0
light.recessed|SFLightRecessed|Home,Objects & Tools|0
light.ribbon.fill|SFLightRibbonFill|Home,Objects & Tools|0
light.ribbon|SFLightRibbon|Home,Objects & Tools|0
light.strip.2.fill|SFLightStrip2Fill|Home,Objects & Tools|0
light.strip.2|SFLightStrip2|Home,Objects & Tools|0
lightbulb.2.fill|SFLightbulb2Fill|Home,Objects & Tools|0
lightbulb.2|SFLightbulb2|Home,Objects & Tools|0
lightbulb.circle.fill|SFLightbulbCircleFill|Home,Multicolor,Objects & Tools|0
lightbulb.circle|SFLightbulbCircle|Draw,Home,Objects & Tools,Variable|0
lightbulb.fill|SFLightbulbFill|Home,Multicolor,Objects & Tools|0
lightbulb.led.fill|SFLightbulbLedFill|Home,Multicolor,Objects & Tools|0
lightbulb.led|SFLightbulbLed|Home,Objects & Tools|0
lightbulb.led.wide.fill|SFLightbulbLedWideFill|Home,Multicolor,Objects & Tools|0
lightbulb.led.wide|SFLightbulbLedWide|Home,Objects & Tools|0
lightbulb.max.fill|SFLightbulbMaxFill|Draw,Home,Objects & Tools|0
lightbulb.max|SFLightbulbMax|Draw,Home,Objects & Tools|0
lightbulb.min.badge.exclamationmark.fill|SFLightbulbMinBadgeExclamationmarkFill|Home,Multicolor,Objects & Tools|0
lightbulb.min.badge.exclamationmark|SFLightbulbMinBadgeExclamationmark|Home,Multicolor,Objects & Tools|0
lightbulb.min.fill|SFLightbulbMinFill|Home,Objects & Tools|0
lightbulb.min|SFLightbulbMin|Home,Objects & Tools|0
lightbulb.slash.fill|SFLightbulbSlashFill|Home,Objects & Tools|0
lightbulb.slash|SFLightbulbSlash|Draw,Home,Objects & Tools|0
lightbulb|SFLightbulb|Home,Objects & Tools|0
lightrail.fill|SFLightrailFill|Transportation|0
lightrail|SFLightrail|Transportation|0
lightspectrum.horizontal|SFLightspectrumHorizontal|Camera & Photos,Multicolor|0
lightswitch.off.fill|SFLightswitchOffFill|Home|0
lightswitch.off.square.fill|SFLightswitchOffSquareFill|Home,Multicolor|0
lightswitch.off.square|SFLightswitchOffSquare|Draw,Home|0
lightswitch.off|SFLightswitchOff|Home|0
lightswitch.on.fill|SFLightswitchOnFill|Home|0
lightswitch.on.square.fill|SFLightswitchOnSquareFill|Home,Multicolor|0
lightswitch.on.square|SFLightswitchOnSquare|Draw,Home|0
lightswitch.on|SFLightswitchOn|Home|0
line.2.horizontal.decrease.circle.fill|SFLine2HorizontalDecreaseCircleFill|Draw,Multicolor|0
line.2.horizontal.decrease.circle|SFLine2HorizontalDecreaseCircle|Draw,Variable|0
line.3.crossed.swirl.circle.fill|SFLine3CrossedSwirlCircleFill||0
line.3.crossed.swirl.circle|SFLine3CrossedSwirlCircle||0
line.3.horizontal.button.angledtop.vertical.right.fill|SFLine3HorizontalButtonAngledtopVerticalRightFill|Gaming,Multicolor|0
line.3.horizontal.button.angledtop.vertical.right|SFLine3HorizontalButtonAngledtopVerticalRight|Gaming|0
line.3.horizontal.circle.fill|SFLine3HorizontalCircleFill|Draw,Gaming,Multicolor|0
line.3.horizontal.circle|SFLine3HorizontalCircle|Draw,Gaming,Variable|0
line.3.horizontal.decrease.circle.fill|SFLine3HorizontalDecreaseCircleFill|Draw,Multicolor|0
line.3.horizontal.decrease.circle|SFLine3HorizontalDecreaseCircle|Draw,Variable|0
line.3.horizontal.decrease|SFLine3HorizontalDecrease|Draw|0
line.3.horizontal|SFLine3Horizontal|Draw|0
line.diagonal|SFLineDiagonal|Communication,Draw|0
line.diagonal.trianglehead.up.right.left.down|SFLineDiagonalTriangleheadUpRightLeftDown|Communication,Draw|0
line.diagonal.trianglehead.up.right|SFLineDiagonalTriangleheadUpRight|Communication,Draw|0
line.horizontal.star.fill.line.horizontal|SFLineHorizontalStarFillLineHorizontal||0
lines.measurement.horizontal.aligned.bottom|SFLinesMeasurementHorizontalAlignedBottom|Draw,Variable|0
lines.measurement.horizontal|SFLinesMeasurementHorizontal|Draw,Variable|0
lines.measurement.vertical|SFLinesMeasurementVertical|Draw,Variable|0
lineweight|SFLineweight||0
link.badge.plus|SFLinkBadgePlus|Multicolor,Objects & Tools|0
link.circle.fill|SFLinkCircleFill|Multicolor,Objects & Tools|0
link.circle|SFLinkCircle|Draw,Multicolor,Objects & Tools,Variable|0
link.icloud.fill|SFLinkIcloudFill|Connectivity,Multicolor|1
link.icloud|SFLinkIcloud|Connectivity|1
link|SFLink|Multicolor,Objects & Tools|0
lirasign.arrow.trianglehead.counterclockwise.rotate.90|SFLirasignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
lirasign.bank.building.fill|SFLirasignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
lirasign.bank.building|SFLirasignBankBuilding|Commerce,Objects & Tools|0
lirasign.circle.fill|SFLirasignCircleFill|Commerce,Indices,Multicolor|0
lirasign.circle|SFLirasignCircle|Commerce,Draw,Indices,Variable|0
lirasign.gauge.chart.lefthalf.righthalf|SFLirasignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
lirasign.gauge.chart.leftthird.topthird.rightthird|SFLirasignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
lirasign.ring.dashed|SFLirasignRingDashed|Commerce,Home,Variable|0
lirasign.ring|SFLirasignRing|Commerce,Draw,Home|0
lirasign.square.fill|SFLirasignSquareFill|Commerce,Indices,Multicolor|0
lirasign.square|SFLirasignSquare|Commerce,Draw,Indices|0
lirasign|SFLirasign|Commerce,Indices|0
list.and.film|SFListAndFilm|Draw|0
list.bullet.badge.ellipsis|SFListBulletBadgeEllipsis|Multicolor,Text Formatting|0
list.bullet.below.rectangle|SFListBulletBelowRectangle|Draw|0
list.bullet.circle.fill|SFListBulletCircleFill|Multicolor,Text Formatting|0
list.bullet.circle|SFListBulletCircle|Draw,Text Formatting,Variable|0
list.bullet.clipboard.fill|SFListBulletClipboardFill|Health,Multicolor,Objects & Tools|0
list.bullet.clipboard|SFListBulletClipboard|Health,Multicolor,Objects & Tools|0
list.bullet.indent|SFListBulletIndent|Draw,Multicolor,Text Formatting|0
list.bullet.rectangle.fill|SFListBulletRectangleFill|Draw,Multicolor|0
list.bullet.rectangle.portrait.fill|SFListBulletRectanglePortraitFill|Draw|0
list.bullet.rectangle.portrait|SFListBulletRectanglePortrait|Draw|0
list.bullet.rectangle|SFListBulletRectangle|Draw|0
list.bullet|SFListBullet|Draw,Multicolor,Text Formatting|0
list.clipboard.fill|SFListClipboardFill|Health,Objects & Tools|0
list.clipboard|SFListClipboard|Health,Multicolor,Objects & Tools|0
list.dash.badge.ellipsis|SFListDashBadgeEllipsis|Multicolor,Text Formatting|0
list.dash.header.rectangle.fill|SFListDashHeaderRectangleFill|Draw,Multicolor|0
list.dash.header.rectangle|SFListDashHeaderRectangle|Draw|0
list.dash|SFListDash|Draw,Multicolor,Text Formatting|0
list.number.badge.ellipsis|SFListNumberBadgeEllipsis|Multicolor,Text Formatting|0
list.number|SFListNumber|Draw,Multicolor,Text Formatting|0
list.star|SFListStar|Draw,Multicolor,Text Formatting|0
list.triangle|SFListTriangle|Draw,Multicolor,Text Formatting|0
livephoto.badge.automatic|SFLivephotoBadgeAutomatic|Camera & Photos,Multicolor|1
livephoto.play|SFLivephotoPlay|Camera & Photos|1
livephoto.slash|SFLivephotoSlash|Camera & Photos|1
livephoto|SFLivephoto|Camera & Photos,Variable|1
lizard.circle.fill|SFLizardCircleFill|Multicolor,Nature|0
lizard.circle|SFLizardCircle|Draw,Nature,Variable|0
lizard.fill|SFLizardFill|Multicolor,Nature|0
lizard|SFLizard|Multicolor,Nature|0
lm.button.horizontal.fill|SFLmButtonHorizontalFill|Gaming,Multicolor|0
lm.button.horizontal|SFLmButtonHorizontal|Gaming|0
location.app.fill|SFLocationAppFill|Arrows,Maps,Multicolor|0
location.app|SFLocationApp|Arrows,Maps|0
location.circle.fill|SFLocationCircleFill|Arrows,Maps,Multicolor|0
location.circle|SFLocationCircle|Arrows,Draw,Maps,Multicolor,Variable|0
location.fill|SFLocationFill|Arrows,Maps,Multicolor|0
location.fill.viewfinder|SFLocationFillViewfinder||0
location.magnifyingglass|SFLocationMagnifyingglass|Objects & Tools|0
location.north.circle.fill|SFLocationNorthCircleFill|Arrows,Maps,Multicolor|0
location.north.circle|SFLocationNorthCircle|Arrows,Draw,Maps,Multicolor,Variable|0
location.north.fill|SFLocationNorthFill|Arrows,Maps,Multicolor|0
location.north.line.fill|SFLocationNorthLineFill|Arrows,Maps,Multicolor|0
location.north.line|SFLocationNorthLine|Arrows,Maps,Multicolor|0
location.north|SFLocationNorth|Arrows,Maps,Multicolor|0
location.slash.circle.fill|SFLocationSlashCircleFill|Arrows,Maps,Multicolor|0
location.slash.circle|SFLocationSlashCircle|Arrows,Draw,Maps,Multicolor,Variable|0
location.slash.fill|SFLocationSlashFill|Arrows,Draw,Maps,Multicolor|0
location.slash|SFLocationSlash|Arrows,Draw,Maps,Multicolor|0
location.square.fill|SFLocationSquareFill|Arrows,Maps,Multicolor|0
location.square|SFLocationSquare|Arrows,Draw,Maps,Multicolor|0
location|SFLocation|Arrows,Maps,Multicolor|0
location.viewfinder|SFLocationViewfinder||0
lock.app.dashed|SFLockAppDashed||0
lock.applewatch|SFLockApplewatch|Devices,Privacy & Security|1
lock.badge.checkmark.fill|SFLockBadgeCheckmarkFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.badge.checkmark|SFLockBadgeCheckmark|Multicolor,Objects & Tools,Privacy & Security|0
lock.badge.clock.fill|SFLockBadgeClockFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.badge.clock|SFLockBadgeClock|Multicolor,Objects & Tools,Privacy & Security|0
lock.badge.xmark.fill|SFLockBadgeXmarkFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.badge.xmark|SFLockBadgeXmark|Multicolor,Objects & Tools,Privacy & Security|0
lock.circle.dotted|SFLockCircleDotted|Objects & Tools,Privacy & Security|0
lock.circle.fill|SFLockCircleFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.circle|SFLockCircle|Draw,Multicolor,Objects & Tools,Privacy & Security,Variable|0
lock.desktopcomputer|SFLockDesktopcomputer|Devices,Privacy & Security|0
lock.display|SFLockDisplay|Devices,Privacy & Security|0
lock.document.fill|SFLockDocumentFill|Objects & Tools,Privacy & Security|0
lock.document|SFLockDocument|Objects & Tools,Privacy & Security|0
lock.fill|SFLockFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.heart.fill|SFLockHeartFill|Health,Multicolor,Privacy & Security|0
lock.heart|SFLockHeart|Draw,Health,Privacy & Security,Variable|0
lock.icloud.fill|SFLockIcloudFill|Connectivity,Multicolor,Privacy & Security|1
lock.icloud|SFLockIcloud|Connectivity,Privacy & Security|1
lock.ipad|SFLockIpad|Devices,Privacy & Security|0
lock.iphone|SFLockIphone|Devices,Privacy & Security|0
lock.laptopcomputer|SFLockLaptopcomputer|Devices,Privacy & Security|0
lock.open.applewatch|SFLockOpenApplewatch|Devices,Privacy & Security|1
lock.open.desktopcomputer|SFLockOpenDesktopcomputer|Devices,Privacy & Security|0
lock.open.display|SFLockOpenDisplay|Devices,Privacy & Security|0
lock.open.fill|SFLockOpenFill|Objects & Tools,Privacy & Security|0
lock.open.ipad|SFLockOpenIpad|Devices,Privacy & Security|0
lock.open.iphone|SFLockOpenIphone|Devices,Privacy & Security|0
lock.open.laptopcomputer|SFLockOpenLaptopcomputer|Devices,Privacy & Security|0
lock.open.rotation|SFLockOpenRotation|Objects & Tools,Privacy & Security|0
lock.open|SFLockOpen|Objects & Tools,Privacy & Security|0
lock.open.trianglebadge.exclamationmark.fill|SFLockOpenTrianglebadgeExclamationmarkFill|Home,Multicolor,Objects & Tools,Privacy & Security|0
lock.open.trianglebadge.exclamationmark|SFLockOpenTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools,Privacy & Security|0
lock.rectangle.dashed|SFLockRectangleDashed||0
lock.rectangle.fill|SFLockRectangleFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.rectangle.on.rectangle.dashed|SFLockRectangleOnRectangleDashed||0
lock.rectangle.on.rectangle.fill|SFLockRectangleOnRectangleFill|Objects & Tools,Privacy & Security|0
lock.rectangle.on.rectangle|SFLockRectangleOnRectangle|Objects & Tools,Privacy & Security|0
lock.rectangle.stack.fill|SFLockRectangleStackFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.rectangle.stack|SFLockRectangleStack|Objects & Tools,Privacy & Security|0
lock.rectangle|SFLockRectangle|Draw,Multicolor,Objects & Tools,Privacy & Security|0
lock.rotation|SFLockRotation|Objects & Tools,Privacy & Security|0
lock.shield.fill|SFLockShieldFill|Objects & Tools,Privacy & Security|0
lock.shield|SFLockShield|Objects & Tools,Privacy & Security|0
lock.slash.fill|SFLockSlashFill|Draw,Multicolor,Objects & Tools,Privacy & Security|0
lock.slash|SFLockSlash|Draw,Multicolor,Objects & Tools,Privacy & Security|0
lock.square.dashed|SFLockSquareDashed|Objects & Tools,Privacy & Security|0
lock.square.fill|SFLockSquareFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.square.stack.fill|SFLockSquareStackFill|Multicolor,Objects & Tools,Privacy & Security|0
lock.square.stack|SFLockSquareStack|Objects & Tools,Privacy & Security|0
lock.square|SFLockSquare|Draw,Multicolor,Objects & Tools,Privacy & Security|0
lock|SFLock|Multicolor,Objects & Tools,Privacy & Security|0
lock.trianglebadge.exclamationmark.fill|SFLockTrianglebadgeExclamationmarkFill|Home,Multicolor,Objects & Tools,Privacy & Security|0
lock.trianglebadge.exclamationmark|SFLockTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools,Privacy & Security|0
long.text.page.and.pencil.fill|SFLongTextPageAndPencilFill||0
long.text.page.and.pencil|SFLongTextPageAndPencil||0
loupe|SFLoupe|Editing|0
lsb.button.angledbottom.horizontal.left.fill|SFLsbButtonAngledbottomHorizontalLeftFill|Gaming,Multicolor|0
lsb.button.angledbottom.horizontal.left|SFLsbButtonAngledbottomHorizontalLeft|Gaming|0
lt.button.roundedtop.horizontal.fill|SFLtButtonRoundedtopHorizontalFill|Gaming,Multicolor|0
lt.button.roundedtop.horizontal|SFLtButtonRoundedtopHorizontal|Gaming|0
lt.circle.fill|SFLtCircleFill|Gaming,Multicolor|0
lt.circle|SFLtCircle|Draw,Gaming,Variable|0
lungs.fill|SFLungsFill|Health,Human,Multicolor|0
lungs|SFLungs|Health,Human,Multicolor|0
m.circle.fill|SFMCircleFill|Indices,Multicolor|0
m.circle|SFMCircle|Draw,Indices,Variable|0
m.square.fill|SFMSquareFill|Indices,Multicolor|0
m.square|SFMSquare|Draw,Indices|0
m1.button.horizontal.fill|SFM1ButtonHorizontalFill|Gaming,Multicolor|0
m1.button.horizontal|SFM1ButtonHorizontal|Gaming|0
m2.button.horizontal.fill|SFM2ButtonHorizontalFill|Gaming,Multicolor|0
m2.button.horizontal|SFM2ButtonHorizontal|Gaming|0
m3.button.horizontal.fill|SFM3ButtonHorizontalFill|Gaming,Multicolor|0
m3.button.horizontal|SFM3ButtonHorizontal|Gaming|0
m4.button.horizontal.fill|SFM4ButtonHorizontalFill|Gaming,Multicolor|0
m4.button.horizontal|SFM4ButtonHorizontal|Gaming|0
macbook.and.applewatch|SFMacbookAndApplewatch|Devices|1
macbook.and.ipad|SFMacbookAndIpad|Devices|1
macbook.and.iphone|SFMacbookAndIphone|Devices|1
macbook.and.ipod|SFMacbookAndIpod|Devices|1
macbook.and.vision.pro|SFMacbookAndVisionPro|Devices|1
macbook.badge.checkmark|SFMacbookBadgeCheckmark|Devices,Multicolor|1
macbook.badge.exclamationmark|SFMacbookBadgeExclamationmark|Devices,Multicolor|1
macbook.badge.shield.checkmark|SFMacbookBadgeShieldCheckmark|Devices,Multicolor|1
macbook.gen1.sizes|SFMacbookGen1Sizes|Devices|1
macbook.gen1|SFMacbookGen1|Devices|1
macbook.gen2.sizes|SFMacbookGen2Sizes|Devices|1
macbook.gen2|SFMacbookGen2|Devices|1
macbook.sizes|SFMacbookSizes|Devices|1
macbook.slash|SFMacbookSlash|Devices|1
macbook|SFMacbook|Devices|1
macbook.trianglebadge.exclamationmark|SFMacbookTrianglebadgeExclamationmark|Devices,Multicolor|1
macmini.badge.checkmark.fill|SFMacminiBadgeCheckmarkFill|Devices,Multicolor|1
macmini.badge.checkmark|SFMacminiBadgeCheckmark|Devices,Multicolor|1
macmini.fill|SFMacminiFill|Devices|1
macmini.gen2.fill|SFMacminiGen2Fill|Devices|1
macmini.gen2|SFMacminiGen2|Devices|1
macmini.gen3.fill|SFMacminiGen3Fill|Devices|1
macmini.gen3|SFMacminiGen3|Devices|1
macmini|SFMacmini|Devices|1
macpro.gen1.fill|SFMacproGen1Fill|Devices|1
macpro.gen1|SFMacproGen1|Devices|1
macpro.gen2.fill|SFMacproGen2Fill|Devices|1
macpro.gen2|SFMacproGen2|Devices|1
macpro.gen3.badge.ckeckmark.fill|SFMacproGen3BadgeCkeckmarkFill|Devices,Multicolor|1
macpro.gen3.badge.ckeckmark|SFMacproGen3BadgeCkeckmark|Devices,Multicolor|1
macpro.gen3.fill|SFMacproGen3Fill|Devices|1
macpro.gen3.server|SFMacproGen3Server|Devices|1
macpro.gen3|SFMacproGen3|Devices|1
macstudio.badge.checkmark.fill|SFMacstudioBadgeCheckmarkFill|Devices,Multicolor|1
macstudio.badge.checkmark|SFMacstudioBadgeCheckmark|Devices,Multicolor|1
macstudio.fill|SFMacstudioFill|Devices|1
macstudio|SFMacstudio|Devices|1
macwindow.and.pointer.arrow|SFMacwindowAndPointerArrow|Multicolor|0
macwindow.badge.plus|SFMacwindowBadgePlus|Multicolor|0
macwindow.on.rectangle|SFMacwindowOnRectangle|Multicolor|0
macwindow.stack|SFMacwindowStack|Multicolor|0
macwindow|SFMacwindow|Multicolor|0
magazine.fill|SFMagazineFill|Objects & Tools|0
magazine|SFMagazine|Objects & Tools|0
magicmouse.fill|SFMagicmouseFill|Devices|1
magicmouse|SFMagicmouse|Devices|1
magnifyingglass.circle.fill|SFMagnifyingglassCircleFill|Multicolor,Objects & Tools|0
magnifyingglass.circle|SFMagnifyingglassCircle|Draw,Objects & Tools,Variable|0
magnifyingglass|SFMagnifyingglass|Objects & Tools|0
magsafe.batterypack.fill|SFMagsafeBatterypackFill|Devices|1
magsafe.batterypack|SFMagsafeBatterypack|Devices|1
mail.and.text.magnifyingglass|SFMailAndTextMagnifyingglass||0
mail.fill|SFMailFill|Draw,Multicolor|0
mail.stack.fill|SFMailStackFill||0
mail.stack|SFMailStack||0
mail|SFMail|Draw|0
malaysianringgitsign.arrow.trianglehead.counterclockwise.rotate.90|SFMalaysianringgitsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
malaysianringgitsign.bank.building.fill|SFMalaysianringgitsignBankBuildingFill|Commerce,Objects & Tools|0
malaysianringgitsign.bank.building|SFMalaysianringgitsignBankBuilding|Commerce,Objects & Tools|0
malaysianringgitsign.circle.fill|SFMalaysianringgitsignCircleFill|Commerce,Indices,Multicolor|0
malaysianringgitsign.circle|SFMalaysianringgitsignCircle|Commerce,Draw,Indices,Variable|0
malaysianringgitsign.gauge.chart.lefthalf.righthalf|SFMalaysianringgitsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
malaysianringgitsign.gauge.chart.leftthird.topthird.rightthird|SFMalaysianringgitsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
malaysianringgitsign.ring.dashed|SFMalaysianringgitsignRingDashed|Commerce,Home,Variable|0
malaysianringgitsign.ring|SFMalaysianringgitsignRing|Commerce,Draw,Home|0
malaysianringgitsign.square.fill|SFMalaysianringgitsignSquareFill|Commerce,Indices,Multicolor|0
malaysianringgitsign.square|SFMalaysianringgitsignSquare|Commerce,Draw,Indices|0
malaysianringgitsign|SFMalaysianringgitsign|Commerce,Indices|0
manatsign.arrow.trianglehead.counterclockwise.rotate.90|SFManatsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
manatsign.bank.building.fill|SFManatsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
manatsign.bank.building|SFManatsignBankBuilding|Commerce,Objects & Tools|0
manatsign.circle.fill|SFManatsignCircleFill|Commerce,Indices,Multicolor|0
manatsign.circle|SFManatsignCircle|Commerce,Draw,Indices,Variable|0
manatsign.gauge.chart.lefthalf.righthalf|SFManatsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
manatsign.gauge.chart.leftthird.topthird.rightthird|SFManatsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
manatsign.ring.dashed|SFManatsignRingDashed|Commerce,Home,Variable|0
manatsign.ring|SFManatsignRing|Commerce,Draw,Home|0
manatsign.square.fill|SFManatsignSquareFill|Commerce,Indices,Multicolor|0
manatsign.square|SFManatsignSquare|Commerce,Draw,Indices|0
manatsign|SFManatsign|Commerce,Indices|0
map.circle.fill|SFMapCircleFill|Maps,Multicolor,Objects & Tools|0
map.circle|SFMapCircle|Draw,Maps,Objects & Tools,Variable|0
map.fill|SFMapFill|Maps,Objects & Tools|0
map|SFMap|Maps,Objects & Tools|0
mappin.and.ellipse.circle.fill|SFMappinAndEllipseCircleFill|Maps,Multicolor,Objects & Tools|0
mappin.and.ellipse.circle|SFMappinAndEllipseCircle|Draw,Maps,Multicolor,Objects & Tools,Variable|0
mappin.and.ellipse|SFMappinAndEllipse|Draw,Maps,Multicolor,Objects & Tools|0
mappin.circle.fill|SFMappinCircleFill|Maps,Multicolor,Objects & Tools|0
mappin.circle|SFMappinCircle|Draw,Maps,Multicolor,Objects & Tools,Variable|0
mappin.slash.circle.fill|SFMappinSlashCircleFill|Maps,Multicolor,Objects & Tools|0
mappin.slash.circle|SFMappinSlashCircle|Draw,Maps,Multicolor,Objects & Tools,Variable|0
mappin.slash|SFMappinSlash|Maps,Multicolor,Objects & Tools|0
mappin.square.fill|SFMappinSquareFill|Maps,Multicolor,Objects & Tools|0
mappin.square|SFMappinSquare|Draw,Maps,Multicolor,Objects & Tools|0
mappin|SFMappin|Maps,Multicolor,Objects & Tools|0
matter.logo|SFMatterLogo||1
mecca|SFMecca|Automotive,Maps|0
medal.fill|SFMedalFill|Fitness,Objects & Tools|0
medal.star.fill|SFMedalStarFill|Objects & Tools|0
medal.star|SFMedalStar|Objects & Tools|0
medal|SFMedal|Fitness,Objects & Tools|0
mediastick|SFMediastick|Devices|0
medical.thermometer.fill|SFMedicalThermometerFill|Health,Objects & Tools|0
medical.thermometer|SFMedicalThermometer|Health,Objects & Tools|0
megaphone.fill|SFMegaphoneFill|Objects & Tools|0
megaphone|SFMegaphone|Objects & Tools|0
memories.badge.checkmark|SFMemoriesBadgeCheckmark|Arrows,Multicolor|0
memories.badge.minus|SFMemoriesBadgeMinus|Arrows,Multicolor|0
memories.badge.plus|SFMemoriesBadgePlus|Arrows,Multicolor|0
memories.badge.xmark|SFMemoriesBadgeXmark|Arrows,Multicolor|0
memories.slash|SFMemoriesSlash|Arrows|0
memories|SFMemories|Arrows,Draw|0
memorychip.fill|SFMemorychipFill|Objects & Tools|0
memorychip|SFMemorychip|Objects & Tools|0
menubar.arrow.down.rectangle|SFMenubarArrowDownRectangle|Draw|0
menubar.arrow.up.rectangle|SFMenubarArrowUpRectangle|Draw|0
menubar.dock.rectangle.badge.record|SFMenubarDockRectangleBadgeRecord||0
menubar.dock.rectangle|SFMenubarDockRectangle||0
menubar.rectangle|SFMenubarRectangle||0
menucard.fill|SFMenucardFill|Multicolor,Objects & Tools|0
menucard|SFMenucard|Objects & Tools|0
message.badge.circle.fill|SFMessageBadgeCircleFill|Communication,Multicolor|1
message.badge.circle|SFMessageBadgeCircle|Communication,Draw,Variable|1
message.badge.fill|SFMessageBadgeFill|Communication,Multicolor|1
message.badge.filled.fill|SFMessageBadgeFilledFill|Communication,Multicolor|1
message.badge|SFMessageBadge|Communication,Multicolor|1
message.badge.waveform.fill|SFMessageBadgeWaveformFill|Communication,Draw,Variable|1
message.badge.waveform|SFMessageBadgeWaveform|Communication,Draw,Variable|1
message.circle.fill|SFMessageCircleFill|Communication,Multicolor|1
message.circle|SFMessageCircle|Communication,Draw,Multicolor,Variable|1
message.fill|SFMessageFill|Communication,Multicolor|1
message|SFMessage|Communication,Multicolor|1
metronome.fill|SFMetronomeFill|Objects & Tools|0
metronome|SFMetronome|Objects & Tools|0
microbe.circle.fill|SFMicrobeCircleFill|Health,Multicolor,Nature|0
microbe.circle|SFMicrobeCircle|Draw,Health,Nature,Variable|0
microbe.fill|SFMicrobeFill|Health,Nature|0
microbe|SFMicrobe|Health,Nature|0
microphone.and.signal.meter.fill|SFMicrophoneAndSignalMeterFill|Communication,Multicolor,Variable|0
microphone.and.signal.meter|SFMicrophoneAndSignalMeter|Communication,Multicolor,Variable|0
microphone.badge.ellipsis.fill|SFMicrophoneBadgeEllipsisFill|Communication,Multicolor|0
microphone.badge.ellipsis|SFMicrophoneBadgeEllipsis|Communication,Multicolor|0
microphone.badge.plus.fill|SFMicrophoneBadgePlusFill|Communication,Multicolor|0
microphone.badge.plus|SFMicrophoneBadgePlus|Communication,Multicolor|0
microphone.badge.xmark.fill|SFMicrophoneBadgeXmarkFill|Communication,Multicolor|0
microphone.badge.xmark|SFMicrophoneBadgeXmark|Communication,Multicolor|0
microphone.circle.fill|SFMicrophoneCircleFill|Communication,Multicolor|0
microphone.circle|SFMicrophoneCircle|Communication,Draw,Multicolor,Variable|0
microphone.fill|SFMicrophoneFill|Communication,Multicolor|0
microphone.slash.circle.fill|SFMicrophoneSlashCircleFill|Communication,Multicolor|0
microphone.slash.circle|SFMicrophoneSlashCircle|Communication,Draw,Multicolor,Variable|0
microphone.slash.fill|SFMicrophoneSlashFill|Communication,Draw,Multicolor|0
microphone.slash|SFMicrophoneSlash|Communication,Draw,Multicolor|0
microphone.square.fill|SFMicrophoneSquareFill|Communication,Multicolor|0
microphone.square|SFMicrophoneSquare|Communication,Draw,Multicolor|0
microphone|SFMicrophone|Communication,Multicolor|0
microwave.fill|SFMicrowaveFill|Home,Objects & Tools|0
microwave|SFMicrowave|Home,Objects & Tools|0
millsign.arrow.trianglehead.counterclockwise.rotate.90|SFMillsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
millsign.bank.building.fill|SFMillsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
millsign.bank.building|SFMillsignBankBuilding|Commerce,Objects & Tools|0
millsign.circle.fill|SFMillsignCircleFill|Commerce,Indices,Multicolor|0
millsign.circle|SFMillsignCircle|Commerce,Draw,Indices,Variable|0
millsign.gauge.chart.lefthalf.righthalf|SFMillsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
millsign.gauge.chart.leftthird.topthird.rightthird|SFMillsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
millsign.ring.dashed|SFMillsignRingDashed|Commerce,Home,Variable|0
millsign.ring|SFMillsignRing|Commerce,Draw,Home|0
millsign.square.fill|SFMillsignSquareFill|Commerce,Indices,Multicolor|0
millsign.square|SFMillsignSquare|Commerce,Draw,Indices|0
millsign|SFMillsign|Commerce,Indices|0
minus.arrow.trianglehead.clockwise|SFMinusArrowTriangleheadClockwise|Draw|0
minus.arrow.trianglehead.counterclockwise|SFMinusArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
minus.circle.fill|SFMinusCircleFill|Draw,Gaming,Math,Multicolor|0
minus.circle|SFMinusCircle|Draw,Gaming,Math,Multicolor,Variable|0
minus.diamond.fill|SFMinusDiamondFill|Draw,Multicolor|0
minus.diamond|SFMinusDiamond|Draw,Multicolor|0
minus.forwardslash.plus|SFMinusForwardslashPlus|Math|0
minus.magnifyingglass|SFMinusMagnifyingglass|Accessibility,Objects & Tools|0
minus.plus.and.fluid.batteryblock|SFMinusPlusAndFluidBatteryblock|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.exclamationmark.fill|SFMinusPlusBatteryblockExclamationmarkFill|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.exclamationmark|SFMinusPlusBatteryblockExclamationmark|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.fill|SFMinusPlusBatteryblockFill|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.slash.fill|SFMinusPlusBatteryblockSlashFill|Automotive,Draw,Multicolor,Objects & Tools|0
minus.plus.batteryblock.slash|SFMinusPlusBatteryblockSlash|Automotive,Draw,Multicolor,Objects & Tools|0
minus.plus.batteryblock.stack.arrowtriangle.left.fill|SFMinusPlusBatteryblockStackArrowtriangleLeftFill|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.stack.arrowtriangle.left|SFMinusPlusBatteryblockStackArrowtriangleLeft|Automotive,Objects & Tools|0
minus.plus.batteryblock.stack.arrowtriangle.right.and.arrowtriangle.left.fill|SFMinusPlusBatteryblockStackArrowtriangleRightAndArrowtriangleLeftFill|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.stack.arrowtriangle.right.and.arrowtriangle.left|SFMinusPlusBatteryblockStackArrowtriangleRightAndArrowtriangleLeft|Automotive,Objects & Tools|0
minus.plus.batteryblock.stack.arrowtriangle.right.fill|SFMinusPlusBatteryblockStackArrowtriangleRightFill|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.stack.arrowtriangle.right|SFMinusPlusBatteryblockStackArrowtriangleRight|Automotive,Objects & Tools|0
minus.plus.batteryblock.stack.exclamationmark.fill|SFMinusPlusBatteryblockStackExclamationmarkFill|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.stack.exclamationmark|SFMinusPlusBatteryblockStackExclamationmark|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.stack.fill|SFMinusPlusBatteryblockStackFill|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock.stack|SFMinusPlusBatteryblockStack|Automotive,Multicolor,Objects & Tools|0
minus.plus.batteryblock|SFMinusPlusBatteryblock|Automotive,Multicolor,Objects & Tools|0
minus.plus.lines.measurement.horizontal.aligned.bottom|SFMinusPlusLinesMeasurementHorizontalAlignedBottom|Variable|0
minus.rectangle.fill|SFMinusRectangleFill|Draw,Math,Multicolor|0
minus.rectangle.portrait.fill|SFMinusRectanglePortraitFill|Draw,Multicolor|0
minus.rectangle.portrait|SFMinusRectanglePortrait|Draw,Multicolor|0
minus.rectangle|SFMinusRectangle|Draw,Math,Multicolor|0
minus.square.fill|SFMinusSquareFill|Draw,Math,Multicolor|0
minus.square|SFMinusSquare|Draw,Math,Multicolor|0
minus|SFMinus|Draw,Gaming,Math,Multicolor|0
mirror.side.left.and.arrow.turn.down.right|SFMirrorSideLeftAndArrowTurnDownRight|Automotive,Draw|0
mirror.side.left.and.heat.waves|SFMirrorSideLeftAndHeatWaves|Automotive|0
mirror.side.left|SFMirrorSideLeft|Automotive|0
mirror.side.right.and.arrow.turn.down.left|SFMirrorSideRightAndArrowTurnDownLeft|Automotive,Draw|0
mirror.side.right.and.heat.waves|SFMirrorSideRightAndHeatWaves|Automotive|0
mirror.side.right|SFMirrorSideRight|Automotive|0
moon.circle.fill|SFMoonCircleFill|Multicolor,Nature,Weather|0
moon.circle|SFMoonCircle|Draw,Nature,Variable,Weather|0
moon.dust.circle.fill|SFMoonDustCircleFill|Multicolor,Nature,Weather|0
moon.dust.circle|SFMoonDustCircle|Draw,Nature,Variable,Weather|0
moon.dust.fill|SFMoonDustFill|Multicolor,Nature,Weather|0
moon.dust|SFMoonDust|Nature,Weather|0
moon.fill|SFMoonFill|Multicolor,Nature,Weather|0
moon.haze.circle.fill|SFMoonHazeCircleFill|Multicolor,Nature,Weather|0
moon.haze.circle|SFMoonHazeCircle|Draw,Nature,Variable,Weather|0
moon.haze.fill|SFMoonHazeFill|Multicolor,Nature,Weather|0
moon.haze|SFMoonHaze|Nature,Weather|0
moon.road.lanes|SFMoonRoadLanes|Automotive|0
moon.stars.circle.fill|SFMoonStarsCircleFill|Multicolor,Nature,Weather|0
moon.stars.circle|SFMoonStarsCircle|Draw,Nature,Variable,Weather|0
moon.stars.fill|SFMoonStarsFill|Multicolor,Nature,Weather|0
moon.stars|SFMoonStars|Nature,Weather|0
moon|SFMoon|Nature,Weather|0
moon.zzz.fill|SFMoonZzzFill|Multicolor|0
moon.zzz|SFMoonZzz||0
moonphase.first.quarter.inverse|SFMoonphaseFirstQuarterInverse|Nature|0
moonphase.first.quarter|SFMoonphaseFirstQuarter|Nature|0
moonphase.full.moon.inverse|SFMoonphaseFullMoonInverse|Nature|0
moonphase.full.moon|SFMoonphaseFullMoon|Nature|0
moonphase.last.quarter.inverse|SFMoonphaseLastQuarterInverse|Nature|0
moonphase.last.quarter|SFMoonphaseLastQuarter|Nature|0
moonphase.new.moon.inverse|SFMoonphaseNewMoonInverse|Nature|0
moonphase.new.moon|SFMoonphaseNewMoon|Nature|0
moonphase.waning.crescent.inverse|SFMoonphaseWaningCrescentInverse|Nature|0
moonphase.waning.crescent|SFMoonphaseWaningCrescent|Nature|0
moonphase.waning.gibbous.inverse|SFMoonphaseWaningGibbousInverse|Nature|0
moonphase.waning.gibbous|SFMoonphaseWaningGibbous|Nature|0
moonphase.waxing.crescent.inverse|SFMoonphaseWaxingCrescentInverse|Nature|0
moonphase.waxing.crescent|SFMoonphaseWaxingCrescent|Nature|0
moonphase.waxing.gibbous.inverse|SFMoonphaseWaxingGibbousInverse|Nature|0
moonphase.waxing.gibbous|SFMoonphaseWaxingGibbous|Nature|0
moonrise.circle.fill|SFMoonriseCircleFill|Multicolor|0
moonrise.circle|SFMoonriseCircle|Draw,Variable|0
moonrise.fill|SFMoonriseFill|Multicolor|0
moonrise|SFMoonrise||0
moonset.circle.fill|SFMoonsetCircleFill|Multicolor|0
moonset.circle|SFMoonsetCircle|Draw,Variable|0
moonset.fill|SFMoonsetFill|Multicolor|0
moonset|SFMoonset||0
moped.fill|SFMopedFill|Transportation|0
moped|SFMoped|Transportation|0
mosaic.fill|SFMosaicFill||0
mosaic|SFMosaic||0
motorcycle.fill|SFMotorcycleFill|Transportation|0
motorcycle|SFMotorcycle|Transportation|0
mount.fill|SFMountFill|Keyboard|0
mount|SFMount|Keyboard|0
mountain.2.circle.fill|SFMountain2CircleFill|Multicolor,Nature|0
mountain.2.circle|SFMountain2Circle|Draw,Nature,Variable|0
mountain.2.fill|SFMountain2Fill|Nature|0
mountain.2|SFMountain2|Nature|0
mouth.fill|SFMouthFill|Human|0
mouth|SFMouth|Human|0
move.3d|SFMove3d|Editing|0
movieclapper.fill|SFMovieclapperFill|Multicolor,Objects & Tools|0
movieclapper|SFMovieclapper|Multicolor,Objects & Tools|0
mph.circle.fill|SFMphCircleFill|Automotive,Multicolor|0
mph.circle|SFMphCircle|Automotive,Draw,Variable|0
mph|SFMph|Automotive|0
mug.fill|SFMugFill|Objects & Tools|0
mug|SFMug|Objects & Tools|0
multiply.circle.fill|SFMultiplyCircleFill|Draw,Math,Multicolor|0
multiply.circle|SFMultiplyCircle|Draw,Math,Variable|0
multiply.square.fill|SFMultiplySquareFill|Draw,Math,Multicolor|0
multiply.square|SFMultiplySquare|Draw,Math|0
multiply|SFMultiply|Draw,Math|0
music.microphone.circle.fill|SFMusicMicrophoneCircleFill|Multicolor,Objects & Tools|0
music.microphone.circle|SFMusicMicrophoneCircle|Draw,Objects & Tools,Variable|0
music.microphone|SFMusicMicrophone|Objects & Tools|0
music.note.arrow.trianglehead.clockwise|SFMusicNoteArrowTriangleheadClockwise|Arrows,Draw|0
music.note.house.fill|SFMusicNoteHouseFill|Media|0
music.note.house|SFMusicNoteHouse|Media|0
music.note.list|SFMusicNoteList|Draw|0
music.note.slash|SFMusicNoteSlash|Draw|0
music.note.square.stack.fill|SFMusicNoteSquareStackFill|Multicolor|0
music.note.square.stack|SFMusicNoteSquareStack||0
music.note|SFMusicNote||0
music.note.tv.fill|SFMusicNoteTvFill|Devices|0
music.note.tv|SFMusicNoteTv|Devices|0
music.pages.fill|SFMusicPagesFill|Multicolor|0
music.pages|SFMusicPages||0
music.quarternote.3|SFMusicQuarternote3||0
mustache.fill|SFMustacheFill|Human|0
mustache|SFMustache|Human|0
n.circle.fill|SFNCircleFill|Indices,Multicolor|0
n.circle|SFNCircle|Draw,Indices,Variable|0
n.square.fill|SFNSquareFill|Indices,Multicolor|0
n.square|SFNSquare|Draw,Indices|0
nairasign.arrow.trianglehead.counterclockwise.rotate.90|SFNairasignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
nairasign.bank.building.fill|SFNairasignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
nairasign.bank.building|SFNairasignBankBuilding|Commerce,Objects & Tools|0
nairasign.circle.fill|SFNairasignCircleFill|Commerce,Indices,Multicolor|0
nairasign.circle|SFNairasignCircle|Commerce,Draw,Indices,Variable|0
nairasign.gauge.chart.lefthalf.righthalf|SFNairasignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
nairasign.gauge.chart.leftthird.topthird.rightthird|SFNairasignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
nairasign.ring.dashed|SFNairasignRingDashed|Commerce,Home,Variable|0
nairasign.ring|SFNairasignRing|Commerce,Draw,Home|0
nairasign.square.fill|SFNairasignSquareFill|Commerce,Indices,Multicolor|0
nairasign.square|SFNairasignSquare|Commerce,Draw,Indices|0
nairasign|SFNairasign|Commerce,Indices|0
network.badge.shield.half.filled|SFNetworkBadgeShieldHalfFilled|Connectivity,Privacy & Security|0
network.slash|SFNetworkSlash|Connectivity,Draw,Multicolor|0
network|SFNetwork|Connectivity,Multicolor|0
newspaper.circle.fill|SFNewspaperCircleFill|Multicolor,Objects & Tools|0
newspaper.circle|SFNewspaperCircle|Draw,Objects & Tools,Variable|0
newspaper.fill|SFNewspaperFill|Multicolor,Objects & Tools|0
newspaper|SFNewspaper|Objects & Tools|0
norwegiankronesign.arrow.trianglehead.counterclockwise.rotate.90|SFNorwegiankronesignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
norwegiankronesign.bank.building.fill|SFNorwegiankronesignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
norwegiankronesign.bank.building|SFNorwegiankronesignBankBuilding|Commerce,Objects & Tools|0
norwegiankronesign.circle.fill|SFNorwegiankronesignCircleFill|Commerce,Indices,Multicolor|0
norwegiankronesign.circle|SFNorwegiankronesignCircle|Commerce,Draw,Indices,Variable|0
norwegiankronesign.gauge.chart.lefthalf.righthalf|SFNorwegiankronesignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
norwegiankronesign.gauge.chart.leftthird.topthird.rightthird|SFNorwegiankronesignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
norwegiankronesign.ring.dashed|SFNorwegiankronesignRingDashed|Commerce,Home,Variable|0
norwegiankronesign.ring|SFNorwegiankronesignRing|Commerce,Draw,Home|0
norwegiankronesign.square.fill|SFNorwegiankronesignSquareFill|Commerce,Indices,Multicolor|0
norwegiankronesign.square|SFNorwegiankronesignSquare|Commerce,Draw,Indices|0
norwegiankronesign|SFNorwegiankronesign|Commerce,Indices|0
nose.fill|SFNoseFill|Human|0
nose|SFNose|Human|0
nosign.app.fill|SFNosignAppFill|Multicolor,Privacy & Security|0
nosign.app|SFNosignApp|Privacy & Security|0
nosign.badge.clock|SFNosignBadgeClock|Multicolor,Privacy & Security|0
nosign|SFNosign|Privacy & Security|0
notequal.circle.fill|SFNotequalCircleFill|Math,Multicolor|0
notequal.circle|SFNotequalCircle|Draw,Math,Variable|0
notequal.square.fill|SFNotequalSquareFill|Math,Multicolor|0
notequal.square|SFNotequalSquare|Draw,Math|0
notequal|SFNotequal|Math|0
number.circle.fill|SFNumberCircleFill|Math,Multicolor|0
number.circle|SFNumberCircle|Draw,Math,Variable|0
number.square.fill|SFNumberSquareFill|Math,Multicolor|0
number.square|SFNumberSquare|Draw,Math|0
number|SFNumber|Math|0
numbers.rectangle.fill|SFNumbersRectangleFill|Multicolor,Text Formatting|0
numbers.rectangle|SFNumbersRectangle|Draw,Text Formatting|0
numbers|SFNumbers|Text Formatting|0
numbersign|SFNumbersign|Text Formatting|0
o.circle.fill|SFOCircleFill|Indices,Multicolor|0
o.circle|SFOCircle|Draw,Indices,Variable|0
o.square.fill|SFOSquareFill|Indices,Multicolor|0
o.square|SFOSquare|Draw,Indices|0
oar.2.crossed.circle.fill|SFOar2CrossedCircleFill|Fitness,Multicolor,Objects & Tools|0
oar.2.crossed.circle|SFOar2CrossedCircle|Draw,Fitness,Objects & Tools,Variable|0
oar.2.crossed|SFOar2Crossed|Fitness,Objects & Tools|0
octagon.bottomhalf.filled|SFOctagonBottomhalfFilled||0
octagon.fill|SFOctagonFill|Shapes|0
octagon.lefthalf.filled|SFOctagonLefthalfFilled||0
octagon.righthalf.filled|SFOctagonRighthalfFilled||0
octagon|SFOctagon|Shapes|0
octagon.tophalf.filled|SFOctagonTophalfFilled||0
oilcan.and.thermometer.fill|SFOilcanAndThermometerFill|Automotive,Objects & Tools|0
oilcan.and.thermometer|SFOilcanAndThermometer|Automotive,Objects & Tools|0
oilcan.fill|SFOilcanFill|Automotive,Multicolor,Objects & Tools|0
oilcan|SFOilcan|Automotive,Multicolor,Objects & Tools|0
opticaldisc.fill|SFOpticaldiscFill|Objects & Tools|0
opticaldisc|SFOpticaldisc|Objects & Tools|0
opticaldiscdrive.fill|SFOpticaldiscdriveFill|Objects & Tools|0
opticaldiscdrive|SFOpticaldiscdrive|Objects & Tools|0
opticid.fill|SFOpticidFill|Multicolor,Variable|1
opticid|SFOpticid|Variable|1
option|SFOption|Draw,Keyboard|0
oval.bottomhalf.filled|SFOvalBottomhalfFilled||0
oval.fill|SFOvalFill|Shapes|0
oval.lefthalf.filled|SFOvalLefthalfFilled||0
oval.portrait.bottomhalf.filled|SFOvalPortraitBottomhalfFilled||0
oval.portrait.fill|SFOvalPortraitFill|Shapes|0
oval.portrait.lefthalf.filled|SFOvalPortraitLefthalfFilled||0
oval.portrait.righthalf.filled|SFOvalPortraitRighthalfFilled||0
oval.portrait|SFOvalPortrait|Shapes|0
oval.portrait.tophalf.filled|SFOvalPortraitTophalfFilled||0
oval.righthalf.filled|SFOvalRighthalfFilled||0
oval|SFOval|Shapes|0
oval.tophalf.filled|SFOvalTophalfFilled||0
oven.fill|SFOvenFill|Home,Objects & Tools|0
oven|SFOven|Home,Objects & Tools|0
p.circle.fill|SFPCircleFill|Indices,Multicolor|0
p.circle|SFPCircle|Draw,Indices,Variable|0
p.square.fill|SFPSquareFill|Indices,Multicolor|0
p.square|SFPSquare|Draw,Indices|0
p1.button.horizontal.fill|SFP1ButtonHorizontalFill|Gaming,Multicolor|0
p1.button.horizontal|SFP1ButtonHorizontal|Gaming|0
p2.button.horizontal.fill|SFP2ButtonHorizontalFill|Gaming,Multicolor|0
p2.button.horizontal|SFP2ButtonHorizontal|Gaming|0
p3.button.horizontal.fill|SFP3ButtonHorizontalFill|Gaming,Multicolor|0
p3.button.horizontal|SFP3ButtonHorizontal|Gaming|0
p4.button.horizontal.fill|SFP4ButtonHorizontalFill|Gaming,Multicolor|0
p4.button.horizontal|SFP4ButtonHorizontal|Gaming|0
pad.header|SFPadHeader|Objects & Tools|0
paddleshifter.left.fill|SFPaddleshifterLeftFill|Gaming|0
paddleshifter.left|SFPaddleshifterLeft|Gaming|0
paddleshifter.right.fill|SFPaddleshifterRightFill|Gaming|0
paddleshifter.right|SFPaddleshifterRight|Gaming|0
paint.bucket.classic|SFPaintBucketClassic||0
paintbrush.fill|SFPaintbrushFill|Editing,Objects & Tools|0
paintbrush.pointed.fill|SFPaintbrushPointedFill|Editing,Objects & Tools|0
paintbrush.pointed|SFPaintbrushPointed|Editing,Objects & Tools|0
paintbrush|SFPaintbrush|Editing,Objects & Tools|0
paintpalette.fill|SFPaintpaletteFill|Multicolor,Objects & Tools|0
paintpalette|SFPaintpalette|Multicolor,Objects & Tools|0
pano.badge.play.fill|SFPanoBadgePlayFill|Multicolor|0
pano.badge.play|SFPanoBadgePlay|Multicolor|0
pano.fill|SFPanoFill||0
pano|SFPano||0
paperclip.badge.ellipsis|SFPaperclipBadgeEllipsis|Multicolor,Objects & Tools|0
paperclip.circle.fill|SFPaperclipCircleFill|Draw,Multicolor,Objects & Tools|0
paperclip.circle|SFPaperclipCircle|Draw,Objects & Tools,Variable|0
paperclip|SFPaperclip|Draw,Multicolor,Objects & Tools|0
paperplane.circle.fill|SFPaperplaneCircleFill|Multicolor,Objects & Tools|0
paperplane.circle|SFPaperplaneCircle|Draw,Objects & Tools,Variable|0
paperplane.fill|SFPaperplaneFill|Objects & Tools|0
paperplane|SFPaperplane|Objects & Tools|0
paragraphsign|SFParagraphsign|Text Formatting|0
parentheses|SFParentheses||0
parkinglight.fill|SFParkinglightFill|Automotive,Draw,Multicolor|0
parkinglight|SFParkinglight|Automotive,Draw,Multicolor|0
parkingsign.brakesignal.slash|SFParkingsignBrakesignalSlash|Automotive,Multicolor|0
parkingsign.brakesignal|SFParkingsignBrakesignal|Automotive,Multicolor|0
parkingsign.circle.fill|SFParkingsignCircleFill|Automotive,Multicolor|0
parkingsign.circle|SFParkingsignCircle|Automotive,Draw,Variable|0
parkingsign.radiowaves.down.right.off|SFParkingsignRadiowavesDownRightOff|Automotive,Variable|0
parkingsign.radiowaves.left.and.right.slash|SFParkingsignRadiowavesLeftAndRightSlash|Automotive|0
parkingsign.radiowaves.left.and.right|SFParkingsignRadiowavesLeftAndRight|Automotive,Draw,Variable|0
parkingsign.radiowaves.right.and.safetycone|SFParkingsignRadiowavesRightAndSafetycone|Automotive,Draw,Variable|0
parkingsign.square.fill|SFParkingsignSquareFill|Automotive,Multicolor|0
parkingsign.square|SFParkingsignSquare|Automotive,Draw|0
parkingsign.steeringwheel|SFParkingsignSteeringwheel|Automotive|0
parkingsign|SFParkingsign|Automotive|0
party.popper.fill|SFPartyPopperFill|Home,Objects & Tools|0
party.popper|SFPartyPopper|Home,Objects & Tools|0
pause.circle.fill|SFPauseCircleFill|Media,Multicolor|0
pause.circle|SFPauseCircle|Draw,Media,Variable|0
pause.fill|SFPauseFill|Media|0
pause.rectangle.fill|SFPauseRectangleFill|Media,Multicolor|0
pause.rectangle|SFPauseRectangle|Draw,Media|0
pause|SFPause|Media|0
pawprint.circle.fill|SFPawprintCircleFill|Multicolor,Nature|0
pawprint.circle|SFPawprintCircle|Draw,Nature,Variable|0
pawprint.fill|SFPawprintFill|Nature|0
pawprint|SFPawprint|Nature|0
pc|SFPc|Devices,Multicolor|0
peacesign|SFPeacesign||0
pedal.accelerator.fill|SFPedalAcceleratorFill|Gaming|0
pedal.accelerator|SFPedalAccelerator|Gaming|0
pedal.brake.fill|SFPedalBrakeFill|Gaming|0
pedal.brake|SFPedalBrake|Gaming|0
pedal.clutch.fill|SFPedalClutchFill|Gaming|0
pedal.clutch|SFPedalClutch|Gaming|0
pedestrian.gate.closed|SFPedestrianGateClosed|Home,Objects & Tools|0
pedestrian.gate.closed.trianglebadge.exclamationmark|SFPedestrianGateClosedTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools|0
pedestrian.gate.open|SFPedestrianGateOpen|Home,Objects & Tools|0
pedestrian.gate.open.trianglebadge.exclamationmark|SFPedestrianGateOpenTrianglebadgeExclamationmark|Home,Multicolor,Objects & Tools|0
pencil.and.list.clipboard|SFPencilAndListClipboard|Health,Multicolor,Objects & Tools|0
pencil.and.outline|SFPencilAndOutline|Draw,Editing,Objects & Tools|0
pencil.and.ruler.fill|SFPencilAndRulerFill|Objects & Tools|0
pencil.and.ruler|SFPencilAndRuler|Objects & Tools|0
pencil.and.scribble|SFPencilAndScribble|Draw,Editing,Objects & Tools|0
pencil.circle.fill|SFPencilCircleFill|Editing,Multicolor,Objects & Tools|0
pencil.circle|SFPencilCircle|Draw,Editing,Objects & Tools,Variable|0
pencil.line|SFPencilLine|Draw,Editing,Objects & Tools|0
pencil.slash|SFPencilSlash|Draw,Editing,Objects & Tools|0
pencil|SFPencil|Editing,Objects & Tools|0
pencil.tip.crop.circle.badge.arrow.forward.fill|SFPencilTipCropCircleBadgeArrowForwardFill|Editing,Multicolor,Objects & Tools|1
pencil.tip.crop.circle.badge.arrow.forward|SFPencilTipCropCircleBadgeArrowForward|Editing,Multicolor,Objects & Tools|1
pencil.tip.crop.circle.badge.minus.fill|SFPencilTipCropCircleBadgeMinusFill|Editing,Multicolor,Objects & Tools|1
pencil.tip.crop.circle.badge.minus|SFPencilTipCropCircleBadgeMinus|Editing,Multicolor,Objects & Tools|1
pencil.tip.crop.circle.badge.plus.fill|SFPencilTipCropCircleBadgePlusFill|Editing,Multicolor,Objects & Tools|1
pencil.tip.crop.circle.badge.plus|SFPencilTipCropCircleBadgePlus|Editing,Multicolor,Objects & Tools|1
pencil.tip.crop.circle.fill|SFPencilTipCropCircleFill|Editing,Multicolor,Objects & Tools|1
pencil.tip.crop.circle|SFPencilTipCropCircle|Editing,Objects & Tools|1
pencil.tip|SFPencilTip|Editing,Objects & Tools|1
pentagon.bottomhalf.filled|SFPentagonBottomhalfFilled||0
pentagon.fill|SFPentagonFill|Shapes|0
pentagon.lefthalf.filled|SFPentagonLefthalfFilled||0
pentagon.righthalf.filled|SFPentagonRighthalfFilled||0
pentagon|SFPentagon|Shapes|0
pentagon.tophalf.filled|SFPentagonTophalfFilled||0
percent|SFPercent|Math|0
person.2.arrow.trianglehead.counterclockwise|SFPerson2ArrowTriangleheadCounterclockwise|Arrows,Draw,Human|0
person.2.badge.fill|SFPerson2BadgeFill|Human,Multicolor|0
person.2.badge.gearshape.fill|SFPerson2BadgeGearshapeFill|Human|0
person.2.badge.gearshape|SFPerson2BadgeGearshape|Human|0
person.2.badge.key.fill|SFPerson2BadgeKeyFill|Human,Objects & Tools|1
person.2.badge.key|SFPerson2BadgeKey|Human,Objects & Tools|1
person.2.badge.minus.fill|SFPerson2BadgeMinusFill|Human,Multicolor|0
person.2.badge.minus|SFPerson2BadgeMinus|Human,Multicolor|0
person.2.badge.plus.fill|SFPerson2BadgePlusFill|Human,Multicolor|0
person.2.badge.plus|SFPerson2BadgePlus|Human,Multicolor|0
person.2.badge|SFPerson2Badge|Human,Multicolor|0
person.2.circle.fill|SFPerson2CircleFill|Human,Multicolor|0
person.2.circle|SFPerson2Circle|Draw,Human,Variable|0
person.2.crop.square.stack.fill|SFPerson2CropSquareStackFill|Human,Multicolor|0
person.2.crop.square.stack|SFPerson2CropSquareStack|Human|0
person.2.fill|SFPerson2Fill|Human|0
person.2.shield.fill|SFPerson2ShieldFill|Human,Multicolor|0
person.2.shield|SFPerson2Shield|Human|0
person.2.slash.fill|SFPerson2SlashFill|Human|0
person.2.slash|SFPerson2Slash|Human|0
person.2|SFPerson2|Human|0
person.2.wave.2.fill|SFPerson2Wave2Fill|Draw,Human,Variable|0
person.2.wave.2|SFPerson2Wave2|Draw,Human,Variable|0
person.3.fill|SFPerson3Fill|Human|0
person.3.sequence.fill|SFPerson3SequenceFill|Human,Variable|0
person.3.sequence|SFPerson3Sequence|Human,Variable|0
person.3|SFPerson3|Human|0
person.and.arrow.left.and.arrow.right.outward|SFPersonAndArrowLeftAndArrowRightOutward|Draw,Human|0
person.and.background.dotted|SFPersonAndBackgroundDotted|Human|0
person.and.background.striped.horizontal|SFPersonAndBackgroundStripedHorizontal|Human|0
person.badge.clock.fill|SFPersonBadgeClockFill|Human,Multicolor|0
person.badge.clock|SFPersonBadgeClock|Human,Multicolor|0
person.badge.key.fill|SFPersonBadgeKeyFill|Human,Objects & Tools|1
person.badge.key|SFPersonBadgeKey|Human,Objects & Tools|1
person.badge.minus|SFPersonBadgeMinus|Human,Multicolor|0
person.badge.plus|SFPersonBadgePlus|Human,Multicolor|0
person.badge.shield.checkmark.fill|SFPersonBadgeShieldCheckmarkFill|Human,Multicolor|0
person.badge.shield.checkmark|SFPersonBadgeShieldCheckmark|Human,Multicolor|0
person.badge.shield.exclamationmark.fill|SFPersonBadgeShieldExclamationmarkFill|Human,Multicolor|0
person.badge.shield.exclamationmark|SFPersonBadgeShieldExclamationmark|Human,Multicolor|0
person.bubble.fill|SFPersonBubbleFill|Communication,Multicolor|0
person.bubble|SFPersonBubble|Communication|0
person.bust.circle.fill|SFPersonBustCircleFill|Human,Multicolor,Objects & Tools|0
person.bust.circle|SFPersonBustCircle|Draw,Human,Objects & Tools,Variable|0
person.bust.fill|SFPersonBustFill|Human,Objects & Tools|0
person.bust|SFPersonBust|Human,Objects & Tools|0
person.checkmark.and.xmark|SFPersonCheckmarkAndXmark|Draw,Human|0
person.circle.fill|SFPersonCircleFill|Human,Multicolor|0
person.circle|SFPersonCircle|Draw,Human,Variable|0
person.crop.artframe|SFPersonCropArtframe|Human|0
person.crop.badge.magnifyingglass.fill|SFPersonCropBadgeMagnifyingglassFill|Human|0
person.crop.badge.magnifyingglass|SFPersonCropBadgeMagnifyingglass|Human|0
person.crop.circle.badge.checkmark|SFPersonCropCircleBadgeCheckmark|Human,Multicolor|0
person.crop.circle.badge.clock.fill|SFPersonCropCircleBadgeClockFill|Human,Multicolor|0
person.crop.circle.badge.clock|SFPersonCropCircleBadgeClock|Human,Multicolor|0
person.crop.circle.badge.ellipsis.fill|SFPersonCropCircleBadgeEllipsisFill|Human,Multicolor|0
person.crop.circle.badge.ellipsis|SFPersonCropCircleBadgeEllipsis|Human,Multicolor|0
person.crop.circle.badge.exclamationmark.fill|SFPersonCropCircleBadgeExclamationmarkFill|Human,Multicolor|0
person.crop.circle.badge.exclamationmark|SFPersonCropCircleBadgeExclamationmark|Human,Multicolor|0
person.crop.circle.badge.fill|SFPersonCropCircleBadgeFill|Human,Multicolor|0
person.crop.circle.badge.minus|SFPersonCropCircleBadgeMinus|Human,Multicolor|0
person.crop.circle.badge.moon.fill|SFPersonCropCircleBadgeMoonFill|Human,Multicolor|0
person.crop.circle.badge.moon|SFPersonCropCircleBadgeMoon|Human,Multicolor|0
person.crop.circle.badge.plus|SFPersonCropCircleBadgePlus|Human,Multicolor|0
person.crop.circle.badge.questionmark.fill|SFPersonCropCircleBadgeQuestionmarkFill|Human,Multicolor|0
person.crop.circle.badge.questionmark|SFPersonCropCircleBadgeQuestionmark|Human,Multicolor|0
person.crop.circle.badge|SFPersonCropCircleBadge|Human,Multicolor|0
person.crop.circle.badge.xmark|SFPersonCropCircleBadgeXmark|Human,Multicolor|0
person.crop.circle.dashed.circle.fill|SFPersonCropCircleDashedCircleFill|Human,Multicolor|0
person.crop.circle.dashed.circle|SFPersonCropCircleDashedCircle|Draw,Human,Variable|0
person.crop.circle.dashed|SFPersonCropCircleDashed|Human|0
person.crop.circle.fill.badge.checkmark|SFPersonCropCircleFillBadgeCheckmark|Human,Multicolor|0
person.crop.circle.fill.badge.minus|SFPersonCropCircleFillBadgeMinus|Human,Multicolor|0
person.crop.circle.fill.badge.plus|SFPersonCropCircleFillBadgePlus|Human,Multicolor|0
person.crop.circle.fill.badge.xmark|SFPersonCropCircleFillBadgeXmark|Human,Multicolor|0
person.crop.circle.fill|SFPersonCropCircleFill|Human,Multicolor|0
person.crop.circle|SFPersonCropCircle|Human|0
person.crop.rectangle.badge.plus.fill|SFPersonCropRectangleBadgePlusFill|Human,Multicolor|0
person.crop.rectangle.badge.plus|SFPersonCropRectangleBadgePlus|Human,Multicolor|0
person.crop.rectangle.fill|SFPersonCropRectangleFill|Human,Multicolor|0
person.crop.rectangle.stack.fill|SFPersonCropRectangleStackFill|Human,Multicolor|0
person.crop.rectangle.stack|SFPersonCropRectangleStack|Human|0
person.crop.rectangle|SFPersonCropRectangle|Human|0
person.crop.square.badge.camera.fill|SFPersonCropSquareBadgeCameraFill|Human|0
person.crop.square.badge.camera|SFPersonCropSquareBadgeCamera|Human|0
person.crop.square.badge.video.fill|SFPersonCropSquareBadgeVideoFill|Human|0
person.crop.square.badge.video|SFPersonCropSquareBadgeVideo|Human|0
person.crop.square.fill|SFPersonCropSquareFill|Human,Multicolor|0
person.crop.square.filled.and.at.rectangle.fill|SFPersonCropSquareFilledAndAtRectangleFill|Human|0
person.crop.square.filled.and.at.rectangle|SFPersonCropSquareFilledAndAtRectangle|Human|0
person.crop.square.on.square.angled.fill|SFPersonCropSquareOnSquareAngledFill|Human|0
person.crop.square.on.square.angled|SFPersonCropSquareOnSquareAngled|Human|0
person.crop.square|SFPersonCropSquare|Human|0
person.fill.and.arrow.left.and.arrow.right.outward|SFPersonFillAndArrowLeftAndArrowRightOutward|Draw,Human|0
person.fill.badge.minus|SFPersonFillBadgeMinus|Human,Multicolor|0
person.fill.badge.plus|SFPersonFillBadgePlus|Human,Multicolor|0
person.fill.checkmark.and.xmark|SFPersonFillCheckmarkAndXmark|Draw,Human|0
person.fill.checkmark|SFPersonFillCheckmark|Draw,Human|0
person.fill.questionmark|SFPersonFillQuestionmark|Human|0
person.fill|SFPersonFill|Human|0
person.fill.turn.down|SFPersonFillTurnDown|Human|0
person.fill.turn.left|SFPersonFillTurnLeft|Human|0
person.fill.turn.right|SFPersonFillTurnRight|Human|0
person.fill.viewfinder|SFPersonFillViewfinder|Human|0
person.fill.xmark|SFPersonFillXmark|Draw,Human|0
person.icloud.fill|SFPersonIcloudFill|Connectivity,Human,Multicolor|1
person.icloud|SFPersonIcloud|Connectivity,Human|1
person.line.dotted.person.fill|SFPersonLineDottedPersonFill|Human|0
person.line.dotted.person|SFPersonLineDottedPerson|Human|0
person.slash.fill|SFPersonSlashFill|Draw,Human|0
person.slash|SFPersonSlash|Draw,Human|0
person.spatialaudio.3d.fill|SFPersonSpatialaudio3dFill|Human,Variable|1
person.spatialaudio.fill|SFPersonSpatialaudioFill|Human,Variable|1
person.spatialaudio.stereo.3d.fill|SFPersonSpatialaudioStereo3dFill|Human,Variable|1
person.spatialaudio.stereo.fill|SFPersonSpatialaudioStereoFill|Human,Variable|1
person|SFPerson|Human|0
person.text.rectangle.fill|SFPersonTextRectangleFill|Human,Multicolor|0
person.text.rectangle|SFPersonTextRectangle|Human|0
person.text.rectangle.trianglebadge.exclamationmark.fill|SFPersonTextRectangleTrianglebadgeExclamationmarkFill|Human,Multicolor|0
person.text.rectangle.trianglebadge.exclamationmark|SFPersonTextRectangleTrianglebadgeExclamationmark|Human,Multicolor|0
person.wave.2.fill|SFPersonWave2Fill|Draw,Human,Variable|0
person.wave.2|SFPersonWave2|Draw,Human,Variable|0
personalhotspot.circle.fill|SFPersonalhotspotCircleFill|Connectivity,Multicolor,Objects & Tools|0
personalhotspot.circle|SFPersonalhotspotCircle|Connectivity,Draw,Objects & Tools,Variable|0
personalhotspot.slash|SFPersonalhotspotSlash|Connectivity,Draw,Objects & Tools|0
personalhotspot|SFPersonalhotspot|Connectivity,Objects & Tools|0
perspective|SFPerspective|Camera & Photos,Draw,Editing|0
peruviansolessign.arrow.trianglehead.counterclockwise.rotate.90|SFPeruviansolessignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
peruviansolessign.bank.building.fill|SFPeruviansolessignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
peruviansolessign.bank.building|SFPeruviansolessignBankBuilding|Commerce,Objects & Tools|0
peruviansolessign.circle.fill|SFPeruviansolessignCircleFill|Commerce,Indices,Multicolor|0
peruviansolessign.circle|SFPeruviansolessignCircle|Commerce,Draw,Indices,Variable|0
peruviansolessign.gauge.chart.lefthalf.righthalf|SFPeruviansolessignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
peruviansolessign.gauge.chart.leftthird.topthird.rightthird|SFPeruviansolessignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
peruviansolessign.ring.dashed|SFPeruviansolessignRingDashed|Commerce,Home,Variable|0
peruviansolessign.ring|SFPeruviansolessignRing|Commerce,Draw,Home|0
peruviansolessign.square.fill|SFPeruviansolessignSquareFill|Commerce,Indices,Multicolor|0
peruviansolessign.square|SFPeruviansolessignSquare|Commerce,Draw,Indices|0
peruviansolessign|SFPeruviansolessign|Commerce,Indices|0
pesetasign.arrow.trianglehead.counterclockwise.rotate.90|SFPesetasignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
pesetasign.bank.building.fill|SFPesetasignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
pesetasign.bank.building|SFPesetasignBankBuilding|Commerce,Objects & Tools|0
pesetasign.circle.fill|SFPesetasignCircleFill|Commerce,Indices,Multicolor|0
pesetasign.circle|SFPesetasignCircle|Commerce,Draw,Indices,Variable|0
pesetasign.gauge.chart.lefthalf.righthalf|SFPesetasignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
pesetasign.gauge.chart.leftthird.topthird.rightthird|SFPesetasignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
pesetasign.ring.dashed|SFPesetasignRingDashed|Commerce,Home,Variable|0
pesetasign.ring|SFPesetasignRing|Commerce,Draw,Home|0
pesetasign.square.fill|SFPesetasignSquareFill|Commerce,Indices,Multicolor|0
pesetasign.square|SFPesetasignSquare|Commerce,Draw,Indices|0
pesetasign|SFPesetasign|Commerce,Indices|0
pesosign.arrow.trianglehead.counterclockwise.rotate.90|SFPesosignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
pesosign.bank.building.fill|SFPesosignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
pesosign.bank.building|SFPesosignBankBuilding|Commerce,Objects & Tools|0
pesosign.circle.fill|SFPesosignCircleFill|Commerce,Indices,Multicolor|0
pesosign.circle|SFPesosignCircle|Commerce,Draw,Indices,Variable|0
pesosign.gauge.chart.lefthalf.righthalf|SFPesosignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
pesosign.gauge.chart.leftthird.topthird.rightthird|SFPesosignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
pesosign.ring.dashed|SFPesosignRingDashed|Commerce,Home,Variable|0
pesosign.ring|SFPesosignRing|Commerce,Draw,Home|0
pesosign.square.fill|SFPesosignSquareFill|Commerce,Indices,Multicolor|0
pesosign.square|SFPesosignSquare|Commerce,Draw,Indices|0
pesosign|SFPesosign|Commerce,Indices|0
pet.carrier.circle.fill|SFPetCarrierCircleFill|Multicolor,Objects & Tools|0
pet.carrier.circle|SFPetCarrierCircle|Draw,Objects & Tools,Variable|0
pet.carrier.fill|SFPetCarrierFill|Multicolor,Objects & Tools|0
pet.carrier|SFPetCarrier|Objects & Tools|0
phone.arrow.down.left.fill|SFPhoneArrowDownLeftFill|Communication,Draw|0
phone.arrow.down.left|SFPhoneArrowDownLeft|Communication,Draw|0
phone.arrow.right.fill|SFPhoneArrowRightFill|Communication,Draw|0
phone.arrow.right|SFPhoneArrowRight|Communication,Draw|0
phone.arrow.up.right.circle.fill|SFPhoneArrowUpRightCircleFill|Communication,Multicolor|0
phone.arrow.up.right.circle|SFPhoneArrowUpRightCircle|Communication,Draw,Variable|0
phone.arrow.up.right.fill|SFPhoneArrowUpRightFill|Communication,Draw|0
phone.arrow.up.right|SFPhoneArrowUpRight|Communication,Draw|0
phone.badge.checkmark|SFPhoneBadgeCheckmark|Communication,Multicolor|0
phone.badge.clock.fill|SFPhoneBadgeClockFill|Communication,Multicolor|0
phone.badge.clock|SFPhoneBadgeClock|Communication,Multicolor|0
phone.badge.plus|SFPhoneBadgePlus|Communication,Multicolor|0
phone.badge.waveform.fill|SFPhoneBadgeWaveformFill|Communication,Draw,Variable|0
phone.badge.waveform|SFPhoneBadgeWaveform|Communication,Draw,Variable|0
phone.bubble.fill|SFPhoneBubbleFill|Communication,Multicolor|0
phone.bubble|SFPhoneBubble|Communication|0
phone.circle.fill|SFPhoneCircleFill|Communication,Multicolor|0
phone.circle|SFPhoneCircle|Communication,Draw,Multicolor,Variable|0
phone.connection.fill|SFPhoneConnectionFill|Communication|0
phone.connection|SFPhoneConnection|Communication|0
phone.down.circle.fill|SFPhoneDownCircleFill|Communication,Multicolor|0
phone.down.circle|SFPhoneDownCircle|Communication,Draw,Multicolor,Variable|0
phone.down.fill|SFPhoneDownFill|Communication,Multicolor|0
phone.down|SFPhoneDown|Communication,Multicolor|0
phone.down.waves.left.and.right|SFPhoneDownWavesLeftAndRight|Communication,Draw,Variable|0
phone.fill.badge.checkmark|SFPhoneFillBadgeCheckmark|Communication,Multicolor|0
phone.fill.badge.plus|SFPhoneFillBadgePlus|Communication,Multicolor|0
phone.fill|SFPhoneFill|Communication,Multicolor|0
phone.pause.circle.fill|SFPhonePauseCircleFill|Communication,Media,Multicolor|0
phone.pause.circle|SFPhonePauseCircle|Communication,Draw,Media,Variable|0
phone.pause.fill|SFPhonePauseFill|Communication,Media|0
phone.pause|SFPhonePause|Communication,Media|0
phone|SFPhone|Communication,Multicolor|0
photo.artframe.circle.fill|SFPhotoArtframeCircleFill|Multicolor,Objects & Tools|0
photo.artframe.circle|SFPhotoArtframeCircle|Draw,Objects & Tools,Variable|0
photo.artframe|SFPhotoArtframe|Objects & Tools|0
photo.badge.arrow.down.fill|SFPhotoBadgeArrowDownFill|Camera & Photos,Multicolor|0
photo.badge.arrow.down|SFPhotoBadgeArrowDown|Camera & Photos,Multicolor|0
photo.badge.checkmark.fill|SFPhotoBadgeCheckmarkFill|Camera & Photos,Multicolor|0
photo.badge.checkmark|SFPhotoBadgeCheckmark|Camera & Photos,Multicolor|0
photo.badge.exclamationmark.fill|SFPhotoBadgeExclamationmarkFill|Camera & Photos,Multicolor|0
photo.badge.exclamationmark|SFPhotoBadgeExclamationmark|Camera & Photos,Multicolor|0
photo.badge.magnifyingglass.fill|SFPhotoBadgeMagnifyingglassFill|Camera & Photos|0
photo.badge.magnifyingglass|SFPhotoBadgeMagnifyingglass|Camera & Photos|0
photo.badge.plus.fill|SFPhotoBadgePlusFill|Camera & Photos,Multicolor|0
photo.badge.plus|SFPhotoBadgePlus|Camera & Photos,Multicolor|0
photo.badge.shield.exclamationmark.fill|SFPhotoBadgeShieldExclamationmarkFill|Camera & Photos,Multicolor|0
photo.badge.shield.exclamationmark|SFPhotoBadgeShieldExclamationmark|Camera & Photos,Multicolor|0
photo.circle.fill|SFPhotoCircleFill|Camera & Photos,Multicolor|0
photo.circle|SFPhotoCircle|Camera & Photos,Draw,Variable|0
photo.fill.on.rectangle.fill|SFPhotoFillOnRectangleFill|Camera & Photos|0
photo.fill|SFPhotoFill|Camera & Photos|0
photo.on.rectangle.angled.fill|SFPhotoOnRectangleAngledFill|Camera & Photos|0
photo.on.rectangle.angled|SFPhotoOnRectangleAngled|Camera & Photos|0
photo.on.rectangle|SFPhotoOnRectangle|Camera & Photos|0
photo.stack.fill|SFPhotoStackFill|Camera & Photos|0
photo.stack|SFPhotoStack|Camera & Photos|0
photo|SFPhoto|Camera & Photos|0
photo.trianglebadge.exclamationmark.fill|SFPhotoTrianglebadgeExclamationmarkFill|Camera & Photos,Multicolor|0
photo.trianglebadge.exclamationmark|SFPhotoTrianglebadgeExclamationmark|Camera & Photos,Multicolor|0
photo.tv|SFPhotoTv|Devices|0
pi.circle.fill|SFPiCircleFill|Math,Multicolor|0
pi.circle|SFPiCircle|Draw,Math,Variable|0
pi.square.fill|SFPiSquareFill|Math,Multicolor|0
pi.square|SFPiSquare|Draw,Math|0
pi|SFPi|Math|0
pianokeys.inverse|SFPianokeysInverse|Objects & Tools|0
pianokeys|SFPianokeys|Objects & Tools|0
pill.circle.fill|SFPillCircleFill|Health,Multicolor,Objects & Tools|0
pill.circle|SFPillCircle|Draw,Health,Objects & Tools,Variable|0
pill.fill|SFPillFill|Health,Multicolor,Objects & Tools|0
pill|SFPill|Health,Objects & Tools|0
pills.circle.fill|SFPillsCircleFill|Health,Multicolor,Objects & Tools|0
pills.circle|SFPillsCircle|Draw,Health,Objects & Tools,Variable|0
pills.fill|SFPillsFill|Health,Multicolor,Objects & Tools|0
pills|SFPills|Health,Objects & Tools|0
pin.circle.fill|SFPinCircleFill|Multicolor,Objects & Tools|0
pin.circle|SFPinCircle|Draw,Multicolor,Objects & Tools,Variable|0
pin.fill|SFPinFill|Multicolor,Objects & Tools|0
pin.slash.fill|SFPinSlashFill|Draw,Multicolor,Objects & Tools|0
pin.slash|SFPinSlash|Draw,Multicolor,Objects & Tools|0
pin.square.fill|SFPinSquareFill|Multicolor,Objects & Tools|0
pin.square|SFPinSquare|Draw,Multicolor,Objects & Tools|0
pin|SFPin|Multicolor,Objects & Tools|0
pip.enter|SFPipEnter||0
pip.exit|SFPipExit||0
pip.fill|SFPipFill||0
pip.remove|SFPipRemove||0
pip|SFPip||0
pip.swap|SFPipSwap||0
pipe.and.drop.fill|SFPipeAndDropFill|Home|0
pipe.and.drop|SFPipeAndDrop|Home|0
placeholdertext.fill|SFPlaceholdertextFill||0
platter.2.filled.ipad.landscape|SFPlatter2FilledIpadLandscape|Devices|0
platter.2.filled.ipad|SFPlatter2FilledIpad|Devices|0
platter.2.filled.iphone.landscape|SFPlatter2FilledIphoneLandscape|Devices|0
platter.2.filled.iphone|SFPlatter2FilledIphone|Devices|0
platter.bottom.applewatch.case|SFPlatterBottomApplewatchCase|Devices|1
platter.filled.bottom.and.arrow.down.iphone|SFPlatterFilledBottomAndArrowDownIphone|Devices,Draw|0
platter.filled.bottom.applewatch.case|SFPlatterFilledBottomApplewatchCase|Devices|1
platter.filled.bottom.iphone|SFPlatterFilledBottomIphone|Devices|0
platter.filled.top.and.arrow.up.iphone|SFPlatterFilledTopAndArrowUpIphone|Devices,Draw|0
platter.filled.top.applewatch.case|SFPlatterFilledTopApplewatchCase|Devices|1
platter.filled.top.iphone|SFPlatterFilledTopIphone|Devices|0
platter.top.applewatch.case|SFPlatterTopApplewatchCase|Devices|1
play.circle.fill|SFPlayCircleFill|Media,Multicolor|0
play.circle|SFPlayCircle|Draw,Media,Variable|0
play.desktopcomputer|SFPlayDesktopcomputer|Devices|0
play.diamond.fill|SFPlayDiamondFill|Media,Multicolor|0
play.diamond|SFPlayDiamond|Media|0
play.display|SFPlayDisplay|Devices|0
play.fill|SFPlayFill|Media|0
play.house.fill|SFPlayHouseFill|Media,Multicolor|0
play.house|SFPlayHouse|Media|0
play.laptopcomputer|SFPlayLaptopcomputer|Devices|0
play.rectangle.fill|SFPlayRectangleFill|Media,Multicolor|0
play.rectangle.on.rectangle.circle.fill|SFPlayRectangleOnRectangleCircleFill|Multicolor|0
play.rectangle.on.rectangle.circle|SFPlayRectangleOnRectangleCircle|Draw,Variable|0
play.rectangle.on.rectangle.fill|SFPlayRectangleOnRectangleFill||0
play.rectangle.on.rectangle|SFPlayRectangleOnRectangle||0
play.rectangle|SFPlayRectangle|Draw,Media|0
play.slash.fill|SFPlaySlashFill|Draw,Media|0
play.slash|SFPlaySlash|Draw,Media|0
play.square.fill|SFPlaySquareFill|Media,Multicolor|0
play.square.stack.fill|SFPlaySquareStackFill|Media,Multicolor|0
play.square.stack|SFPlaySquareStack|Media|0
play.square|SFPlaySquare|Draw,Media|0
play|SFPlay|Media|0
play.tv.fill|SFPlayTvFill|Devices|0
play.tv|SFPlayTv|Devices|0
playpause.circle.fill|SFPlaypauseCircleFill|Media,Multicolor|0
playpause.circle|SFPlaypauseCircle|Draw,Media,Variable|0
playpause.fill|SFPlaypauseFill|Media|0
playpause|SFPlaypause|Media|0
playstation.logo|SFPlaystationLogo|Gaming|1
plus.app.fill|SFPlusAppFill|Draw,Multicolor|0
plus.app|SFPlusApp|Draw|0
plus.arrow.trianglehead.clockwise|SFPlusArrowTriangleheadClockwise|Arrows,Draw,Media|0
plus.arrow.trianglehead.counterclockwise|SFPlusArrowTriangleheadCounterclockwise|Arrows,Draw,Media|0
plus.bubble.fill|SFPlusBubbleFill|Communication,Draw,Multicolor|0
plus.bubble|SFPlusBubble|Communication,Draw|0
plus.capsule.fill|SFPlusCapsuleFill|Draw,Multicolor|0
plus.capsule|SFPlusCapsule|Draw|0
plus.circle.dashed|SFPlusCircleDashed|Draw|0
plus.circle.fill|SFPlusCircleFill|Draw,Gaming,Math,Multicolor|0
plus.circle|SFPlusCircle|Draw,Gaming,Math,Multicolor,Variable|0
plus.diamond.fill|SFPlusDiamondFill|Draw,Multicolor|0
plus.diamond|SFPlusDiamond|Draw,Multicolor|0
plus.forwardslash.minus|SFPlusForwardslashMinus|Math|0
plus.magnifyingglass|SFPlusMagnifyingglass|Accessibility,Objects & Tools|0
plus.message.fill|SFPlusMessageFill|Communication,Draw,Multicolor|1
plus.message|SFPlusMessage|Communication,Draw|1
plus.minus.capsule.fill|SFPlusMinusCapsuleFill|Draw,Multicolor|0
plus.minus.capsule|SFPlusMinusCapsule|Draw|0
plus.rectangle.fill.on.rectangle.fill|SFPlusRectangleFillOnRectangleFill||0
plus.rectangle.fill|SFPlusRectangleFill|Draw,Math,Multicolor|0
plus.rectangle.on.folder.fill|SFPlusRectangleOnFolderFill|Objects & Tools|0
plus.rectangle.on.folder|SFPlusRectangleOnFolder|Objects & Tools|0
plus.rectangle.on.rectangle|SFPlusRectangleOnRectangle||0
plus.rectangle.portrait.fill|SFPlusRectanglePortraitFill|Draw,Multicolor|0
plus.rectangle.portrait|SFPlusRectanglePortrait|Draw,Multicolor|0
plus.rectangle|SFPlusRectangle|Draw,Math,Multicolor|0
plus.square.dashed|SFPlusSquareDashed|Draw|0
plus.square.fill.on.square.fill|SFPlusSquareFillOnSquareFill|Draw|0
plus.square.fill|SFPlusSquareFill|Draw,Math,Multicolor|0
plus.square.on.square|SFPlusSquareOnSquare|Draw|0
plus.square|SFPlusSquare|Draw,Math,Multicolor|0
plus|SFPlus|Draw,Gaming,Math,Multicolor|0
plus.viewfinder|SFPlusViewfinder|Camera & Photos,Draw|0
plusminus.circle.fill|SFPlusminusCircleFill|Camera & Photos,Draw,Math,Multicolor|0
plusminus.circle|SFPlusminusCircle|Camera & Photos,Draw,Math,Variable|0
plusminus|SFPlusminus|Camera & Photos,Draw,Math|0
point.3.connected.trianglepath.dotted|SFPoint3ConnectedTrianglepathDotted|Multicolor|0
point.3.filled.connected.trianglepath.dotted|SFPoint3FilledConnectedTrianglepathDotted|Multicolor|0
point.bottomleft.filled.forward.to.point.topright.scurvepath|SFPointBottomleftFilledForwardToPointToprightScurvepath|Draw,Maps|0
point.bottomleft.forward.to.arrow.triangle.scurvepath.fill|SFPointBottomleftForwardToArrowTriangleScurvepathFill|Draw,Maps|0
point.bottomleft.forward.to.arrow.triangle.scurvepath|SFPointBottomleftForwardToArrowTriangleScurvepath|Draw,Maps|0
point.bottomleft.forward.to.arrow.triangle.uturn.scurvepath.fill|SFPointBottomleftForwardToArrowTriangleUturnScurvepathFill|Maps|0
point.bottomleft.forward.to.arrow.triangle.uturn.scurvepath|SFPointBottomleftForwardToArrowTriangleUturnScurvepath|Maps|0
point.bottomleft.forward.to.point.topright.filled.scurvepath|SFPointBottomleftForwardToPointToprightFilledScurvepath|Draw,Maps|0
point.bottomleft.forward.to.point.topright.scurvepath.fill|SFPointBottomleftForwardToPointToprightScurvepathFill|Draw,Maps|0
point.bottomleft.forward.to.point.topright.scurvepath|SFPointBottomleftForwardToPointToprightScurvepath|Draw,Maps|0
point.forward.to.point.capsulepath.fill|SFPointForwardToPointCapsulepathFill|Maps|0
point.forward.to.point.capsulepath|SFPointForwardToPointCapsulepath|Maps|0
point.topleft.down.to.point.bottomright.curvepath.fill|SFPointTopleftDownToPointBottomrightCurvepathFill|Draw,Maps|0
point.topleft.down.to.point.bottomright.curvepath|SFPointTopleftDownToPointBottomrightCurvepath|Draw,Maps|0
point.topleft.down.to.point.bottomright.filled.curvepath|SFPointTopleftDownToPointBottomrightFilledCurvepath|Draw,Maps|0
point.topleft.filled.down.to.point.bottomright.curvepath|SFPointTopleftFilledDownToPointBottomrightCurvepath|Draw,Maps|0
point.topright.arrow.triangle.backward.to.point.bottomleft.filled.scurvepath|SFPointToprightArrowTriangleBackwardToPointBottomleftFilledScurvepath|Draw,Maps|0
point.topright.arrow.triangle.backward.to.point.bottomleft.scurvepath.fill|SFPointToprightArrowTriangleBackwardToPointBottomleftScurvepathFill|Draw,Maps|0
point.topright.arrow.triangle.backward.to.point.bottomleft.scurvepath|SFPointToprightArrowTriangleBackwardToPointBottomleftScurvepath|Draw,Maps|0
point.topright.filled.arrow.triangle.backward.to.point.bottomleft.scurvepath|SFPointToprightFilledArrowTriangleBackwardToPointBottomleftScurvepath|Draw,Maps|0
pointer.arrow.and.square.on.square.dashed|SFPointerArrowAndSquareOnSquareDashed|Accessibility|0
pointer.arrow.click.2|SFPointerArrowClick2|Accessibility,Variable|0
pointer.arrow.click.badge.clock|SFPointerArrowClickBadgeClock|Accessibility,Multicolor|0
pointer.arrow.click|SFPointerArrowClick|Accessibility|0
pointer.arrow.ipad.and.square.on.square.dashed|SFPointerArrowIpadAndSquareOnSquareDashed|Accessibility|0
pointer.arrow.ipad.rays|SFPointerArrowIpadRays|Accessibility,Draw|0
pointer.arrow.ipad.slash.square.fill|SFPointerArrowIpadSlashSquareFill|Multicolor|0
pointer.arrow.ipad.slash.square|SFPointerArrowIpadSlashSquare|Draw|0
pointer.arrow.ipad.slash|SFPointerArrowIpadSlash|Draw|0
pointer.arrow.ipad.square.fill|SFPointerArrowIpadSquareFill|Multicolor|0
pointer.arrow.ipad.square|SFPointerArrowIpadSquare|Draw|0
pointer.arrow.ipad|SFPointerArrowIpad||0
pointer.arrow.motionlines.click|SFPointerArrowMotionlinesClick|Accessibility|0
pointer.arrow.motionlines|SFPointerArrowMotionlines|Accessibility,Draw|0
pointer.arrow.rays|SFPointerArrowRays|Accessibility,Draw|0
pointer.arrow.slash.square.fill|SFPointerArrowSlashSquareFill|Multicolor|0
pointer.arrow.slash.square|SFPointerArrowSlashSquare|Draw|0
pointer.arrow.slash|SFPointerArrowSlash|Draw|0
pointer.arrow.square.fill|SFPointerArrowSquareFill|Multicolor|0
pointer.arrow.square|SFPointerArrowSquare|Draw|0
pointer.arrow|SFPointerArrow||0
polishzlotysign.arrow.trianglehead.counterclockwise.rotate.90|SFPolishzlotysignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
polishzlotysign.bank.building.fill|SFPolishzlotysignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
polishzlotysign.bank.building|SFPolishzlotysignBankBuilding|Commerce,Objects & Tools|0
polishzlotysign.circle.fill|SFPolishzlotysignCircleFill|Commerce,Indices,Multicolor|0
polishzlotysign.circle|SFPolishzlotysignCircle|Commerce,Draw,Indices,Variable|0
polishzlotysign.gauge.chart.lefthalf.righthalf|SFPolishzlotysignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
polishzlotysign.gauge.chart.leftthird.topthird.rightthird|SFPolishzlotysignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
polishzlotysign.ring.dashed|SFPolishzlotysignRingDashed|Commerce,Home,Variable|0
polishzlotysign.ring|SFPolishzlotysignRing|Commerce,Draw,Home|0
polishzlotysign.square.fill|SFPolishzlotysignSquareFill|Commerce,Indices,Multicolor|0
polishzlotysign.square|SFPolishzlotysignSquare|Commerce,Draw,Indices|0
polishzlotysign|SFPolishzlotysign|Commerce,Indices|0
popcorn.circle.fill|SFPopcornCircleFill|Home,Multicolor,Objects & Tools|0
popcorn.circle|SFPopcornCircle|Draw,Home,Objects & Tools,Variable|0
popcorn.fill|SFPopcornFill|Home,Objects & Tools|0
popcorn|SFPopcorn|Home,Objects & Tools|0
power.circle.fill|SFPowerCircleFill|Draw,Keyboard,Multicolor|0
power.circle|SFPowerCircle|Draw,Keyboard,Variable|0
power.dotted|SFPowerDotted|Keyboard|0
power|SFPower|Draw,Keyboard|0
powercord.fill|SFPowercordFill|Automotive,Objects & Tools|0
powercord|SFPowercord|Automotive,Objects & Tools|0
powermeter|SFPowermeter|Automotive,Draw|0
poweroff|SFPoweroff|Draw|0
poweron|SFPoweron|Draw|0
poweroutlet.strip.fill|SFPoweroutletStripFill|Home,Objects & Tools|0
poweroutlet.strip|SFPoweroutletStrip|Home,Objects & Tools|0
poweroutlet.type.a.fill|SFPoweroutletTypeAFill|Home|0
poweroutlet.type.a.square.fill|SFPoweroutletTypeASquareFill|Home,Multicolor|0
poweroutlet.type.a.square|SFPoweroutletTypeASquare|Draw,Home|0
poweroutlet.type.a|SFPoweroutletTypeA|Home|0
poweroutlet.type.b.fill|SFPoweroutletTypeBFill|Home|0
poweroutlet.type.b.square.fill|SFPoweroutletTypeBSquareFill|Home,Multicolor|0
poweroutlet.type.b.square|SFPoweroutletTypeBSquare|Draw,Home|0
poweroutlet.type.b|SFPoweroutletTypeB|Home|0
poweroutlet.type.c.fill|SFPoweroutletTypeCFill|Home|0
poweroutlet.type.c.square.fill|SFPoweroutletTypeCSquareFill|Home,Multicolor|0
poweroutlet.type.c.square|SFPoweroutletTypeCSquare|Draw,Home|0
poweroutlet.type.c|SFPoweroutletTypeC|Home|0
poweroutlet.type.d.fill|SFPoweroutletTypeDFill|Home|0
poweroutlet.type.d.square.fill|SFPoweroutletTypeDSquareFill|Home,Multicolor|0
poweroutlet.type.d.square|SFPoweroutletTypeDSquare|Draw,Home|0
poweroutlet.type.d|SFPoweroutletTypeD|Home|0
poweroutlet.type.e.fill|SFPoweroutletTypeEFill|Home|0
poweroutlet.type.e.square.fill|SFPoweroutletTypeESquareFill|Home,Multicolor|0
poweroutlet.type.e.square|SFPoweroutletTypeESquare|Draw,Home|0
poweroutlet.type.e|SFPoweroutletTypeE|Home|0
poweroutlet.type.f.fill|SFPoweroutletTypeFFill|Home|0
poweroutlet.type.f.square.fill|SFPoweroutletTypeFSquareFill|Home,Multicolor|0
poweroutlet.type.f.square|SFPoweroutletTypeFSquare|Draw,Home|0
poweroutlet.type.f|SFPoweroutletTypeF|Home|0
poweroutlet.type.g.fill|SFPoweroutletTypeGFill|Home|0
poweroutlet.type.g.square.fill|SFPoweroutletTypeGSquareFill|Home,Multicolor|0
poweroutlet.type.g.square|SFPoweroutletTypeGSquare|Draw,Home|0
poweroutlet.type.g|SFPoweroutletTypeG|Home|0
poweroutlet.type.h.fill|SFPoweroutletTypeHFill|Home|0
poweroutlet.type.h.square.fill|SFPoweroutletTypeHSquareFill|Home,Multicolor|0
poweroutlet.type.h.square|SFPoweroutletTypeHSquare|Draw,Home|0
poweroutlet.type.h|SFPoweroutletTypeH|Home|0
poweroutlet.type.i.fill|SFPoweroutletTypeIFill|Home|0
poweroutlet.type.i.square.fill|SFPoweroutletTypeISquareFill|Home,Multicolor|0
poweroutlet.type.i.square|SFPoweroutletTypeISquare|Draw,Home|0
poweroutlet.type.i|SFPoweroutletTypeI|Home|0
poweroutlet.type.j.fill|SFPoweroutletTypeJFill|Home|0
poweroutlet.type.j.square.fill|SFPoweroutletTypeJSquareFill|Home,Multicolor|0
poweroutlet.type.j.square|SFPoweroutletTypeJSquare|Draw,Home|0
poweroutlet.type.j|SFPoweroutletTypeJ|Home|0
poweroutlet.type.k.fill|SFPoweroutletTypeKFill|Home|0
poweroutlet.type.k.square.fill|SFPoweroutletTypeKSquareFill|Home,Multicolor|0
poweroutlet.type.k.square|SFPoweroutletTypeKSquare|Draw,Home|0
poweroutlet.type.k|SFPoweroutletTypeK|Home|0
poweroutlet.type.l.fill|SFPoweroutletTypeLFill|Home|0
poweroutlet.type.l.square.fill|SFPoweroutletTypeLSquareFill|Home,Multicolor|0
poweroutlet.type.l.square|SFPoweroutletTypeLSquare|Draw,Home|0
poweroutlet.type.l|SFPoweroutletTypeL|Home|0
poweroutlet.type.m.fill|SFPoweroutletTypeMFill|Home|0
poweroutlet.type.m.square.fill|SFPoweroutletTypeMSquareFill|Home,Multicolor|0
poweroutlet.type.m.square|SFPoweroutletTypeMSquare|Draw,Home|0
poweroutlet.type.m|SFPoweroutletTypeM|Home|0
poweroutlet.type.n.fill|SFPoweroutletTypeNFill|Home|0
poweroutlet.type.n.square.fill|SFPoweroutletTypeNSquareFill|Home,Multicolor|0
poweroutlet.type.n.square|SFPoweroutletTypeNSquare|Draw,Home|0
poweroutlet.type.n|SFPoweroutletTypeN|Home|0
poweroutlet.type.o.fill|SFPoweroutletTypeOFill|Home|0
poweroutlet.type.o.square.fill|SFPoweroutletTypeOSquareFill|Home,Multicolor|0
poweroutlet.type.o.square|SFPoweroutletTypeOSquare|Draw,Home|0
poweroutlet.type.o|SFPoweroutletTypeO|Home|0
powerplug.fill|SFPowerplugFill|Objects & Tools|0
powerplug.portrait.fill|SFPowerplugPortraitFill|Objects & Tools|0
powerplug.portrait|SFPowerplugPortrait|Objects & Tools|0
powerplug|SFPowerplug|Objects & Tools|0
powersleep|SFPowersleep||0
printer.dotmatrix.fill|SFPrinterDotmatrixFill|Devices,Objects & Tools|0
printer.dotmatrix.filled.and.paper.inverse|SFPrinterDotmatrixFilledAndPaperInverse|Devices,Objects & Tools|0
printer.dotmatrix.filled.and.paper|SFPrinterDotmatrixFilledAndPaper|Devices,Objects & Tools|0
printer.dotmatrix.inverse|SFPrinterDotmatrixInverse|Devices,Objects & Tools|0
printer.dotmatrix|SFPrinterDotmatrix|Devices,Objects & Tools|0
printer.fill|SFPrinterFill|Devices,Objects & Tools|0
printer.filled.and.paper.inverse|SFPrinterFilledAndPaperInverse|Devices,Objects & Tools|0
printer.filled.and.paper|SFPrinterFilledAndPaper|Devices,Objects & Tools|0
printer.inverse|SFPrinterInverse|Devices,Objects & Tools|0
printer|SFPrinter|Devices,Objects & Tools|0
progress.indicator|SFProgressIndicator|Draw,Variable|0
projective|SFProjective|Keyboard|0
purchased.circle.fill|SFPurchasedCircleFill|Multicolor|0
purchased.circle|SFPurchasedCircle|Draw,Variable|0
purchased|SFPurchased||0
puzzlepiece.extension.fill|SFPuzzlepieceExtensionFill|Objects & Tools|0
puzzlepiece.extension|SFPuzzlepieceExtension|Objects & Tools|0
puzzlepiece.fill|SFPuzzlepieceFill|Objects & Tools|0
puzzlepiece|SFPuzzlepiece|Objects & Tools|0
pyramid.fill|SFPyramidFill||0
pyramid|SFPyramid||0
q.circle.fill|SFQCircleFill|Indices,Multicolor|0
q.circle|SFQCircle|Draw,Indices,Variable|0
q.square.fill|SFQSquareFill|Indices,Multicolor|0
q.square|SFQSquare|Draw,Indices|0
qrcode|SFQrcode||0
qrcode.viewfinder|SFQrcodeViewfinder||0
questionmark.app.dashed|SFQuestionmarkAppDashed||0
questionmark.app.fill|SFQuestionmarkAppFill|Multicolor|0
questionmark.app|SFQuestionmarkApp||0
questionmark.bubble.fill|SFQuestionmarkBubbleFill|Communication,Multicolor|0
questionmark.bubble|SFQuestionmarkBubble|Communication|0
questionmark.circle.dashed|SFQuestionmarkCircleDashed||0
questionmark.circle.fill|SFQuestionmarkCircleFill|Indices,Multicolor|0
questionmark.circle|SFQuestionmarkCircle|Draw,Indices,Multicolor,Variable|0
questionmark.diamond.fill|SFQuestionmarkDiamondFill|Multicolor|0
questionmark.diamond|SFQuestionmarkDiamond|Multicolor|0
questionmark.folder.fill|SFQuestionmarkFolderFill|Objects & Tools|0
questionmark.folder|SFQuestionmarkFolder|Objects & Tools|0
questionmark.key.filled|SFQuestionmarkKeyFilled|Automotive,Objects & Tools,Privacy & Security|0
questionmark.message.fill|SFQuestionmarkMessageFill|Communication,Multicolor|1
questionmark.message|SFQuestionmarkMessage|Communication|1
questionmark.square.dashed|SFQuestionmarkSquareDashed||0
questionmark.square.fill|SFQuestionmarkSquareFill|Indices,Multicolor|0
questionmark.square|SFQuestionmarkSquare|Draw,Indices,Multicolor|0
questionmark|SFQuestionmark|Multicolor|0
questionmark.text.page.fill|SFQuestionmarkTextPageFill||0
questionmark.text.page|SFQuestionmarkTextPage||0
questionmark.video.fill|SFQuestionmarkVideoFill|Communication|1
questionmark.video|SFQuestionmarkVideo|Communication|1
quote.bubble.fill|SFQuoteBubbleFill|Accessibility,Communication,Multicolor|0
quote.bubble|SFQuoteBubble|Accessibility,Communication|0
quote.closing|SFQuoteClosing|Communication|0
quote.opening|SFQuoteOpening|Communication|0
quotelevel|SFQuotelevel|Draw,Multicolor,Text Formatting|0
r.button.roundedbottom.horizontal.fill|SFRButtonRoundedbottomHorizontalFill|Gaming,Multicolor|0
r.button.roundedbottom.horizontal|SFRButtonRoundedbottomHorizontal|Gaming|0
r.circle.fill|SFRCircleFill|Gaming,Indices,Multicolor|0
r.circle|SFRCircle|Draw,Gaming,Indices,Variable|0
r.joystick.fill|SFRJoystickFill|Gaming|0
r.joystick.press.down.fill|SFRJoystickPressDownFill|Gaming|0
r.joystick.press.down|SFRJoystickPressDown|Gaming|0
r.joystick|SFRJoystick|Gaming|0
r.joystick.tilt.down.fill|SFRJoystickTiltDownFill|Gaming|0
r.joystick.tilt.down|SFRJoystickTiltDown|Gaming|0
r.joystick.tilt.left.fill|SFRJoystickTiltLeftFill|Gaming|0
r.joystick.tilt.left|SFRJoystickTiltLeft|Gaming|0
r.joystick.tilt.right.fill|SFRJoystickTiltRightFill|Gaming|0
r.joystick.tilt.right|SFRJoystickTiltRight|Gaming|0
r.joystick.tilt.up.fill|SFRJoystickTiltUpFill|Gaming|0
r.joystick.tilt.up|SFRJoystickTiltUp|Gaming|0
r.square.fill|SFRSquareFill|Indices,Multicolor|0
r.square.on.square.fill|SFRSquareOnSquareFill||0
r.square.on.square|SFRSquareOnSquare||0
r.square|SFRSquare|Draw,Indices|0
r1.button.roundedbottom.horizontal.fill|SFR1ButtonRoundedbottomHorizontalFill|Gaming,Multicolor|0
r1.button.roundedbottom.horizontal|SFR1ButtonRoundedbottomHorizontal|Gaming|0
r1.circle.fill|SFR1CircleFill|Gaming,Multicolor|0
r1.circle|SFR1Circle|Draw,Gaming,Variable|0
r2.button.angledtop.vertical.right.fill|SFR2ButtonAngledtopVerticalRightFill|Gaming,Multicolor|0
r2.button.angledtop.vertical.right|SFR2ButtonAngledtopVerticalRight|Gaming|0
r2.button.roundedtop.horizontal.fill|SFR2ButtonRoundedtopHorizontalFill|Gaming,Multicolor|0
r2.button.roundedtop.horizontal|SFR2ButtonRoundedtopHorizontal|Gaming|0
r2.circle.fill|SFR2CircleFill|Gaming,Multicolor|0
r2.circle|SFR2Circle|Draw,Gaming,Variable|0
r3.button.angledbottom.horizontal.right.fill|SFR3ButtonAngledbottomHorizontalRightFill|Gaming,Multicolor|0
r3.button.angledbottom.horizontal.right|SFR3ButtonAngledbottomHorizontalRight|Gaming|0
r4.button.horizontal.fill|SFR4ButtonHorizontalFill|Gaming,Multicolor|0
r4.button.horizontal|SFR4ButtonHorizontal|Gaming|0
radicand.squareroot|SFRadicandSquareroot|Draw,Math|0
radio.fill|SFRadioFill|Objects & Tools|0
radio|SFRadio|Objects & Tools|0
rainbow|SFRainbow|Draw,Multicolor,Nature,Variable,Weather|0
rays|SFRays|Draw,Variable|0
rb.button.roundedbottom.horizontal.fill|SFRbButtonRoundedbottomHorizontalFill|Gaming,Multicolor|0
rb.button.roundedbottom.horizontal|SFRbButtonRoundedbottomHorizontal|Gaming|0
rb.circle.fill|SFRbCircleFill|Gaming,Multicolor|0
rb.circle|SFRbCircle|Draw,Gaming,Variable|0
receipt.fill|SFReceiptFill|Objects & Tools|0
receipt|SFReceipt|Objects & Tools|0
record.circle.fill|SFRecordCircleFill|Media,Multicolor|0
record.circle|SFRecordCircle|Draw,Media,Variable|0
recordingtape.circle.fill|SFRecordingtapeCircleFill|Communication,Multicolor|0
recordingtape.circle|SFRecordingtapeCircle|Communication,Draw,Variable|0
recordingtape|SFRecordingtape|Communication|0
rectangle.2.swap|SFRectangle2Swap||0
rectangle.3.group.bubble.fill|SFRectangle3GroupBubbleFill|Accessibility,Communication,Multicolor|0
rectangle.3.group.bubble|SFRectangle3GroupBubble|Accessibility,Communication|0
rectangle.3.group.dashed|SFRectangle3GroupDashed||0
rectangle.3.group.fill|SFRectangle3GroupFill||0
rectangle.3.group|SFRectangle3Group||0
rectangle.and.arrow.up.right.and.arrow.down.left.slash|SFRectangleAndArrowUpRightAndArrowDownLeftSlash|Camera & Photos|0
rectangle.and.arrow.up.right.and.arrow.down.left|SFRectangleAndArrowUpRightAndArrowDownLeft|Camera & Photos|0
rectangle.and.hand.point.up.left.fill|SFRectangleAndHandPointUpLeftFill|Human|0
rectangle.and.hand.point.up.left.filled|SFRectangleAndHandPointUpLeftFilled|Human|0
rectangle.and.hand.point.up.left|SFRectangleAndHandPointUpLeft|Human|0
rectangle.and.paperclip|SFRectangleAndPaperclip|Draw,Objects & Tools|0
rectangle.and.pencil.and.ellipsis|SFRectangleAndPencilAndEllipsis|Editing,Objects & Tools,Variable|0
rectangle.and.text.magnifyingglass|SFRectangleAndTextMagnifyingglass|Accessibility|0
rectangle.arrowtriangle.2.inward|SFRectangleArrowtriangle2Inward||0
rectangle.arrowtriangle.2.outward|SFRectangleArrowtriangle2Outward||0
rectangle.badge.checkmark|SFRectangleBadgeCheckmark|Multicolor|0
rectangle.badge.minus|SFRectangleBadgeMinus|Multicolor|0
rectangle.badge.person.crop|SFRectangleBadgePersonCrop|Human,Multicolor|0
rectangle.badge.plus|SFRectangleBadgePlus|Multicolor|0
rectangle.badge.sparkles.fill|SFRectangleBadgeSparklesFill||0
rectangle.badge.sparkles|SFRectangleBadgeSparkles||0
rectangle.badge.xmark|SFRectangleBadgeXmark|Multicolor|0
rectangle.bottomhalf.filled|SFRectangleBottomhalfFilled||0
rectangle.compress.vertical|SFRectangleCompressVertical||0
rectangle.connected.to.line.below|SFRectangleConnectedToLineBelow||0
rectangle.dashed.and.paperclip|SFRectangleDashedAndPaperclip|Draw,Objects & Tools|0
rectangle.dashed.badge.record|SFRectangleDashedBadgeRecord|Editing|0
rectangle.dashed|SFRectangleDashed|Editing|0
rectangle.expand.diagonal|SFRectangleExpandDiagonal||0
rectangle.expand.vertical|SFRectangleExpandVertical||0
rectangle.fill.badge.checkmark|SFRectangleFillBadgeCheckmark|Multicolor|0
rectangle.fill.badge.minus|SFRectangleFillBadgeMinus|Multicolor|0
rectangle.fill.badge.person.crop|SFRectangleFillBadgePersonCrop|Human,Multicolor|0
rectangle.fill.badge.plus|SFRectangleFillBadgePlus|Multicolor|0
rectangle.fill.badge.xmark|SFRectangleFillBadgeXmark|Multicolor|0
rectangle.fill.on.rectangle.angled.fill|SFRectangleFillOnRectangleAngledFill||0
rectangle.fill.on.rectangle.fill|SFRectangleFillOnRectangleFill|Gaming|0
rectangle.fill|SFRectangleFill|Shapes|0
rectangle.filled.and.hand.point.up.left|SFRectangleFilledAndHandPointUpLeft|Human|0
rectangle.grid.1x2.fill|SFRectangleGrid1x2Fill||0
rectangle.grid.1x2|SFRectangleGrid1x2||0
rectangle.grid.1x3.fill|SFRectangleGrid1x3Fill||0
rectangle.grid.1x3|SFRectangleGrid1x3||0
rectangle.grid.2x2.fill|SFRectangleGrid2x2Fill||0
rectangle.grid.2x2|SFRectangleGrid2x2||0
rectangle.grid.3x1.fill|SFRectangleGrid3x1Fill||0
rectangle.grid.3x1|SFRectangleGrid3x1||0
rectangle.grid.3x2.fill|SFRectangleGrid3x2Fill||0
rectangle.grid.3x2|SFRectangleGrid3x2||0
rectangle.grid.3x3.fill|SFRectangleGrid3x3Fill||0
rectangle.grid.3x3|SFRectangleGrid3x3||0
rectangle.landscape.rotate.slash|SFRectangleLandscapeRotateSlash|Devices,Editing,Objects & Tools|0
rectangle.landscape.rotate|SFRectangleLandscapeRotate|Devices,Draw,Editing,Objects & Tools|0
rectangle.leadinghalf.filled|SFRectangleLeadinghalfFilled||0
rectangle.lefthalf.filled|SFRectangleLefthalfFilled||0
rectangle.on.rectangle.angled|SFRectangleOnRectangleAngled||0
rectangle.on.rectangle.badge.gearshape|SFRectangleOnRectangleBadgeGearshape||0
rectangle.on.rectangle.button.angledtop.vertical.left.fill|SFRectangleOnRectangleButtonAngledtopVerticalLeftFill|Gaming,Multicolor|0
rectangle.on.rectangle.button.angledtop.vertical.left|SFRectangleOnRectangleButtonAngledtopVerticalLeft|Gaming|0
rectangle.on.rectangle.circle.fill|SFRectangleOnRectangleCircleFill|Gaming,Multicolor|0
rectangle.on.rectangle.circle|SFRectangleOnRectangleCircle|Draw,Gaming,Variable|0
rectangle.on.rectangle.dashed|SFRectangleOnRectangleDashed||0
rectangle.on.rectangle.slash.circle.fill|SFRectangleOnRectangleSlashCircleFill|Multicolor|0
rectangle.on.rectangle.slash.circle|SFRectangleOnRectangleSlashCircle|Draw,Variable|0
rectangle.on.rectangle.slash.fill|SFRectangleOnRectangleSlashFill||0
rectangle.on.rectangle.slash|SFRectangleOnRectangleSlash||0
rectangle.on.rectangle.square.fill|SFRectangleOnRectangleSquareFill|Gaming,Multicolor|0
rectangle.on.rectangle.square|SFRectangleOnRectangleSquare|Draw,Gaming|0
rectangle.on.rectangle|SFRectangleOnRectangle|Gaming|0
rectangle.pattern.checkered|SFRectanglePatternCheckered||0
rectangle.portrait.and.arrow.forward.fill|SFRectanglePortraitAndArrowForwardFill||0
rectangle.portrait.and.arrow.forward|SFRectanglePortraitAndArrowForward|Draw|0
rectangle.portrait.and.arrow.right.fill|SFRectanglePortraitAndArrowRightFill||0
rectangle.portrait.and.arrow.right|SFRectanglePortraitAndArrowRight|Draw|0
rectangle.portrait.arrowtriangle.2.inward|SFRectanglePortraitArrowtriangle2Inward||0
rectangle.portrait.arrowtriangle.2.outward|SFRectanglePortraitArrowtriangle2Outward||0
rectangle.portrait.badge.plus.fill|SFRectanglePortraitBadgePlusFill|Multicolor|0
rectangle.portrait.badge.plus|SFRectanglePortraitBadgePlus|Multicolor|0
rectangle.portrait.bottomhalf.filled|SFRectanglePortraitBottomhalfFilled||0
rectangle.portrait.fill|SFRectanglePortraitFill|Shapes|0
rectangle.portrait.lefthalf.filled|SFRectanglePortraitLefthalfFilled||0
rectangle.portrait.on.rectangle.portrait.angled.fill|SFRectanglePortraitOnRectanglePortraitAngledFill||0
rectangle.portrait.on.rectangle.portrait.angled|SFRectanglePortraitOnRectanglePortraitAngled||0
rectangle.portrait.on.rectangle.portrait.fill|SFRectanglePortraitOnRectanglePortraitFill||0
rectangle.portrait.on.rectangle.portrait.slash.fill|SFRectanglePortraitOnRectanglePortraitSlashFill||0
rectangle.portrait.on.rectangle.portrait.slash|SFRectanglePortraitOnRectanglePortraitSlash||0
rectangle.portrait.on.rectangle.portrait|SFRectanglePortraitOnRectanglePortrait||0
rectangle.portrait.righthalf.filled|SFRectanglePortraitRighthalfFilled||0
rectangle.portrait.rotate.slash|SFRectanglePortraitRotateSlash|Devices,Editing,Objects & Tools|0
rectangle.portrait.rotate|SFRectanglePortraitRotate|Devices,Draw,Editing,Objects & Tools|0
rectangle.portrait.slash.fill|SFRectanglePortraitSlashFill|Draw|0
rectangle.portrait.slash|SFRectanglePortraitSlash|Draw|0
rectangle.portrait.split.2x1.fill|SFRectanglePortraitSplit2x1Fill||0
rectangle.portrait.split.2x1.slash.fill|SFRectanglePortraitSplit2x1SlashFill|Draw|0
rectangle.portrait.split.2x1.slash|SFRectanglePortraitSplit2x1Slash|Draw|0
rectangle.portrait.split.2x1|SFRectanglePortraitSplit2x1||0
rectangle.portrait|SFRectanglePortrait|Shapes|0
rectangle.portrait.tophalf.filled|SFRectanglePortraitTophalfFilled||0
rectangle.ratio.16.to.9.fill|SFRectangleRatio16To9Fill||0
rectangle.ratio.16.to.9|SFRectangleRatio16To9||0
rectangle.ratio.3.to.4.fill|SFRectangleRatio3To4Fill||0
rectangle.ratio.3.to.4|SFRectangleRatio3To4||0
rectangle.ratio.4.to.3.fill|SFRectangleRatio4To3Fill||0
rectangle.ratio.4.to.3|SFRectangleRatio4To3||0
rectangle.ratio.9.to.16.fill|SFRectangleRatio9To16Fill||0
rectangle.ratio.9.to.16|SFRectangleRatio9To16||0
rectangle.righthalf.filled|SFRectangleRighthalfFilled||0
rectangle.slash.fill|SFRectangleSlashFill|Draw|0
rectangle.slash|SFRectangleSlash|Draw|0
rectangle.split.1x2.fill|SFRectangleSplit1x2Fill||0
rectangle.split.1x2|SFRectangleSplit1x2||0
rectangle.split.2x1.fill|SFRectangleSplit2x1Fill||0
rectangle.split.2x1.slash.fill|SFRectangleSplit2x1SlashFill|Draw|0
rectangle.split.2x1.slash|SFRectangleSplit2x1Slash|Draw|0
rectangle.split.2x1|SFRectangleSplit2x1||0
rectangle.split.2x2.fill|SFRectangleSplit2x2Fill||0
rectangle.split.2x2|SFRectangleSplit2x2||0
rectangle.split.3x1.fill|SFRectangleSplit3x1Fill||0
rectangle.split.3x1|SFRectangleSplit3x1||0
rectangle.split.3x3.fill|SFRectangleSplit3x3Fill||0
rectangle.split.3x3|SFRectangleSplit3x3||0
rectangle.stack.badge.minus|SFRectangleStackBadgeMinus|Multicolor|0
rectangle.stack.badge.person.crop.fill|SFRectangleStackBadgePersonCropFill|Human,Multicolor|0
rectangle.stack.badge.person.crop|SFRectangleStackBadgePersonCrop|Human,Multicolor|0
rectangle.stack.badge.play.fill|SFRectangleStackBadgePlayFill|Multicolor|0
rectangle.stack.badge.play|SFRectangleStackBadgePlay|Multicolor|0
rectangle.stack.badge.plus|SFRectangleStackBadgePlus|Multicolor|0
rectangle.stack.fill.badge.minus|SFRectangleStackFillBadgeMinus|Multicolor|0
rectangle.stack.fill.badge.plus|SFRectangleStackFillBadgePlus|Multicolor|0
rectangle.stack.fill|SFRectangleStackFill|Camera & Photos|0
rectangle.stack.slash.fill|SFRectangleStackSlashFill|Camera & Photos|0
rectangle.stack.slash|SFRectangleStackSlash|Camera & Photos|0
rectangle.stack|SFRectangleStack|Camera & Photos|0
rectangle|SFRectangle|Draw,Shapes|0
rectangle.tophalf.filled|SFRectangleTophalfFilled||0
rectangle.trailinghalf.filled|SFRectangleTrailinghalfFilled||0
refrigerator.fill|SFRefrigeratorFill|Home,Objects & Tools|0
refrigerator|SFRefrigerator|Home,Objects & Tools|0
repeat.1.circle.fill|SFRepeat1CircleFill|Arrows,Media,Multicolor|0
repeat.1.circle|SFRepeat1Circle|Arrows,Draw,Media,Variable|0
repeat.1|SFRepeat1|Arrows,Draw,Media|0
repeat.badge.xmark|SFRepeatBadgeXmark|Arrows,Media,Multicolor|0
repeat.circle.fill|SFRepeatCircleFill|Arrows,Draw,Media,Multicolor|0
repeat.circle|SFRepeatCircle|Arrows,Draw,Media,Variable|0
repeat|SFRepeat|Arrows,Draw,Media|0
restart.circle.fill|SFRestartCircleFill|Multicolor|0
restart.circle|SFRestartCircle|Draw,Variable|0
restart|SFRestart||0
retarder.brakesignal.and.exclamationmark|SFRetarderBrakesignalAndExclamationmark|Automotive|0
retarder.brakesignal.slash|SFRetarderBrakesignalSlash|Automotive|0
retarder.brakesignal|SFRetarderBrakesignal|Automotive|0
return.left|SFReturnLeft|Arrows|0
return.right|SFReturnRight|Arrows|0
return|SFReturn|Arrows|0
rhombus.fill|SFRhombusFill|Shapes|0
rhombus|SFRhombus|Shapes|0
richtext.page.fill|SFRichtextPageFill||0
richtext.page|SFRichtextPage||0
right.circle.fill|SFRightCircleFill|Multicolor|0
right.circle|SFRightCircle|Draw,Variable|0
right|SFRight||0
righttriangle.fill|SFRighttriangleFill|Camera & Photos|0
righttriangle.split.diagonal.fill|SFRighttriangleSplitDiagonalFill|Camera & Photos|0
righttriangle.split.diagonal|SFRighttriangleSplitDiagonal|Camera & Photos|0
righttriangle|SFRighttriangle|Camera & Photos|0
ring.dashed|SFRingDashed|Commerce,Home,Variable|0
ring|SFRing|Commerce,Draw,Home|0
rm.button.horizontal.fill|SFRmButtonHorizontalFill|Gaming,Multicolor|0
rm.button.horizontal|SFRmButtonHorizontal|Gaming|0
road.lane.arrowtriangle.2.inward|SFRoadLaneArrowtriangle2Inward|Automotive|0
road.lane.arrowtriangle.2.outward|SFRoadLaneArrowtriangle2Outward|Automotive|0
road.lanes.curved.left|SFRoadLanesCurvedLeft|Automotive|0
road.lanes.curved.right|SFRoadLanesCurvedRight|Automotive|0
road.lanes|SFRoadLanes|Automotive|0
robotic.vacuum.and.arrowtriangle.up.fill|SFRoboticVacuumAndArrowtriangleUpFill|Home,Objects & Tools|0
robotic.vacuum.and.arrowtriangle.up|SFRoboticVacuumAndArrowtriangleUp|Home,Objects & Tools|0
robotic.vacuum.and.ellipsis.fill|SFRoboticVacuumAndEllipsisFill|Home,Objects & Tools,Variable|0
robotic.vacuum.and.ellipsis|SFRoboticVacuumAndEllipsis|Home,Objects & Tools,Variable|0
robotic.vacuum.fill|SFRoboticVacuumFill|Home,Objects & Tools|0
robotic.vacuum|SFRoboticVacuum|Home,Objects & Tools|0
roller.shade.closed|SFRollerShadeClosed|Home|0
roller.shade.open|SFRollerShadeOpen|Home|0
roman.shade.closed|SFRomanShadeClosed|Home|0
roman.shade.open|SFRomanShadeOpen|Home|0
rosette|SFRosette|Objects & Tools|0
rotate.3d.circle.fill|SFRotate3dCircleFill|Editing,Multicolor|0
rotate.3d.circle|SFRotate3dCircle|Draw,Editing,Variable|0
rotate.3d.fill|SFRotate3dFill|Editing|0
rotate.3d|SFRotate3d|Editing|0
rotate.left.fill|SFRotateLeftFill|Draw,Editing|0
rotate.left|SFRotateLeft|Draw,Editing|0
rotate.right.fill|SFRotateRightFill|Draw,Editing|0
rotate.right|SFRotateRight|Draw,Editing|0
rsb.button.angledbottom.horizontal.right.fill|SFRsbButtonAngledbottomHorizontalRightFill|Gaming,Multicolor|0
rsb.button.angledbottom.horizontal.right|SFRsbButtonAngledbottomHorizontalRight|Gaming|0
rt.button.roundedtop.horizontal.fill|SFRtButtonRoundedtopHorizontalFill|Gaming,Multicolor|0
rt.button.roundedtop.horizontal|SFRtButtonRoundedtopHorizontal|Gaming|0
rt.circle.fill|SFRtCircleFill|Gaming,Multicolor|0
rt.circle|SFRtCircle|Draw,Gaming,Variable|0
rublesign.arrow.trianglehead.counterclockwise.rotate.90|SFRublesignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
rublesign.bank.building.fill|SFRublesignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
rublesign.bank.building|SFRublesignBankBuilding|Commerce,Objects & Tools|0
rublesign.circle.fill|SFRublesignCircleFill|Commerce,Indices,Multicolor|0
rublesign.circle|SFRublesignCircle|Commerce,Draw,Indices,Variable|0
rublesign.gauge.chart.lefthalf.righthalf|SFRublesignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
rublesign.gauge.chart.leftthird.topthird.rightthird|SFRublesignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
rublesign.ring.dashed|SFRublesignRingDashed|Commerce,Home,Variable|0
rublesign.ring|SFRublesignRing|Commerce,Draw,Home|0
rublesign.square.fill|SFRublesignSquareFill|Commerce,Indices,Multicolor|0
rublesign.square|SFRublesignSquare|Commerce,Draw,Indices|0
rublesign|SFRublesign|Commerce,Indices|0
rugbyball.circle.fill|SFRugbyballCircleFill|Fitness,Multicolor,Objects & Tools|0
rugbyball.circle|SFRugbyballCircle|Draw,Fitness,Objects & Tools,Variable|0
rugbyball.fill|SFRugbyballFill|Fitness,Objects & Tools|0
rugbyball|SFRugbyball|Fitness,Objects & Tools|0
ruler.fill|SFRulerFill|Objects & Tools|0
ruler|SFRuler|Objects & Tools|0
rupeesign.arrow.trianglehead.counterclockwise.rotate.90|SFRupeesignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
rupeesign.bank.building.fill|SFRupeesignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
rupeesign.bank.building|SFRupeesignBankBuilding|Commerce,Objects & Tools|0
rupeesign.circle.fill|SFRupeesignCircleFill|Commerce,Indices,Multicolor|0
rupeesign.circle|SFRupeesignCircle|Commerce,Draw,Indices,Variable|0
rupeesign.gauge.chart.lefthalf.righthalf|SFRupeesignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
rupeesign.gauge.chart.leftthird.topthird.rightthird|SFRupeesignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
rupeesign.ring.dashed|SFRupeesignRingDashed|Commerce,Home,Variable|0
rupeesign.ring|SFRupeesignRing|Commerce,Draw,Home|0
rupeesign.square.fill|SFRupeesignSquareFill|Commerce,Indices,Multicolor|0
rupeesign.square|SFRupeesignSquare|Commerce,Draw,Indices|0
rupeesign|SFRupeesign|Commerce,Indices|0
s.circle.fill|SFSCircleFill|Indices,Multicolor|0
s.circle|SFSCircle|Draw,Indices,Variable|0
s.square.fill|SFSSquareFill|Indices,Multicolor|0
s.square|SFSSquare|Draw,Indices|0
safari.fill|SFSafariFill|Multicolor|1
safari|SFSafari||1
sailboat.circle.fill|SFSailboatCircleFill|Multicolor,Objects & Tools,Transportation|0
sailboat.circle|SFSailboatCircle|Draw,Objects & Tools,Transportation,Variable|0
sailboat.fill|SFSailboatFill|Objects & Tools,Transportation|0
sailboat|SFSailboat|Objects & Tools,Transportation|0
scale.3d|SFScale3d|Editing|0
scalemass.fill|SFScalemassFill|Objects & Tools|0
scalemass|SFScalemass|Objects & Tools|0
scanner.fill|SFScannerFill|Devices,Objects & Tools|0
scanner|SFScanner|Devices,Objects & Tools|0
scissors.badge.ellipsis|SFScissorsBadgeEllipsis|Editing,Multicolor,Objects & Tools|0
scissors.circle.fill|SFScissorsCircleFill|Editing,Multicolor,Objects & Tools|0
scissors.circle|SFScissorsCircle|Draw,Editing,Objects & Tools,Variable|0
scissors|SFScissors|Editing,Objects & Tools|0
scooter|SFScooter|Transportation|0
scope|SFScope|Camera & Photos,Draw|0
screwdriver.fill|SFScrewdriverFill|Objects & Tools|0
screwdriver|SFScrewdriver|Objects & Tools|0
scribble|SFScribble|Draw,Editing|0
scribble.variable|SFScribbleVariable|Draw,Editing|0
scroll.fill|SFScrollFill|Objects & Tools|0
scroll|SFScroll|Objects & Tools|0
sdcard.fill|SFSdcardFill|Objects & Tools|0
sdcard|SFSdcard|Objects & Tools|0
seal.fill|SFSealFill|Privacy & Security,Shapes|0
seal|SFSeal|Privacy & Security,Shapes|0
selection.pin.in.out|SFSelectionPinInOut|Editing|0
sensor.fill|SFSensorFill|Home,Variable|0
sensor.radiowaves.left.and.right.fill|SFSensorRadiowavesLeftAndRightFill|Devices,Draw,Variable|0
sensor.radiowaves.left.and.right|SFSensorRadiowavesLeftAndRight|Devices,Draw,Variable|0
sensor|SFSensor|Home,Variable|0
sensor.tag.radiowaves.forward.fill|SFSensorTagRadiowavesForwardFill|Draw,Objects & Tools,Variable|0
sensor.tag.radiowaves.forward|SFSensorTagRadiowavesForward|Draw,Objects & Tools,Variable|0
server.rack|SFServerRack|Devices|0
service.dog.fill|SFServiceDogFill|Accessibility,Nature|0
service.dog|SFServiceDog|Accessibility,Nature|0
shadow|SFShadow|Multicolor,Text Formatting|0
sharedwithyou.circle.fill|SFSharedwithyouCircleFill|Human,Multicolor|1
sharedwithyou.circle|SFSharedwithyouCircle|Draw,Human,Variable|1
sharedwithyou.slash|SFSharedwithyouSlash|Human|1
sharedwithyou|SFSharedwithyou|Human|1
shareplay.slash|SFShareplaySlash|Human|1
shareplay|SFShareplay|Draw,Human,Variable|1
shazam.logo.fill|SFShazamLogoFill|Multicolor|1
shazam.logo|SFShazamLogo||1
shekelsign.arrow.trianglehead.counterclockwise.rotate.90|SFShekelsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
shekelsign.bank.building.fill|SFShekelsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
shekelsign.bank.building|SFShekelsignBankBuilding|Commerce,Objects & Tools|0
shekelsign.circle.fill|SFShekelsignCircleFill|Commerce,Indices,Multicolor|0
shekelsign.circle|SFShekelsignCircle|Commerce,Draw,Indices,Variable|0
shekelsign.gauge.chart.lefthalf.righthalf|SFShekelsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
shekelsign.gauge.chart.leftthird.topthird.rightthird|SFShekelsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
shekelsign.ring.dashed|SFShekelsignRingDashed|Commerce,Home,Variable|0
shekelsign.ring|SFShekelsignRing|Commerce,Draw,Home|0
shekelsign.square.fill|SFShekelsignSquareFill|Commerce,Indices,Multicolor|0
shekelsign.square|SFShekelsignSquare|Commerce,Draw,Indices|0
shekelsign|SFShekelsign|Commerce,Indices|0
shield.fill|SFShieldFill|Objects & Tools,Privacy & Security,Shapes|0
shield.lefthalf.filled.badge.checkmark|SFShieldLefthalfFilledBadgeCheckmark|Multicolor,Objects & Tools,Privacy & Security|0
shield.lefthalf.filled.slash|SFShieldLefthalfFilledSlash|Draw,Objects & Tools,Privacy & Security|0
shield.lefthalf.filled|SFShieldLefthalfFilled|Objects & Tools,Privacy & Security|0
shield.lefthalf.filled.trianglebadge.exclamationmark|SFShieldLefthalfFilledTrianglebadgeExclamationmark|Multicolor,Objects & Tools,Privacy & Security|0
shield.pattern.checkered|SFShieldPatternCheckered|Objects & Tools|0
shield.righthalf.filled|SFShieldRighthalfFilled|Objects & Tools,Privacy & Security|0
shield.slash.fill|SFShieldSlashFill|Draw,Objects & Tools,Privacy & Security|0
shield.slash|SFShieldSlash|Draw,Objects & Tools,Privacy & Security|0
shield|SFShield|Objects & Tools,Privacy & Security,Shapes|0
shift.fill|SFShiftFill|Keyboard|0
shift|SFShift|Keyboard|0
shippingbox.and.arrow.backward.fill|SFShippingboxAndArrowBackwardFill|Draw,Objects & Tools|0
shippingbox.and.arrow.backward|SFShippingboxAndArrowBackward|Draw,Objects & Tools|0
shippingbox.circle.fill|SFShippingboxCircleFill|Multicolor,Objects & Tools|0
shippingbox.circle|SFShippingboxCircle|Draw,Objects & Tools,Variable|0
shippingbox.fill|SFShippingboxFill|Objects & Tools|0
shippingbox|SFShippingbox|Objects & Tools|0
shoe.2.fill|SFShoe2Fill|Objects & Tools|0
shoe.2|SFShoe2|Objects & Tools|0
shoe.arrow.trianglehead.up.and.down.fill|SFShoeArrowTriangleheadUpAndDownFill|Automotive,Draw,Objects & Tools|0
shoe.arrow.trianglehead.up.and.down|SFShoeArrowTriangleheadUpAndDown|Automotive,Draw,Objects & Tools|0
shoe.arrow.trianglehead.up.right.circle.fill|SFShoeArrowTriangleheadUpRightCircleFill|Automotive,Multicolor,Objects & Tools|0
shoe.arrow.trianglehead.up.right.circle|SFShoeArrowTriangleheadUpRightCircle|Automotive,Draw,Objects & Tools,Variable|0
shoe.arrow.trianglehead.up.right.fill|SFShoeArrowTriangleheadUpRightFill|Automotive,Objects & Tools|0
shoe.arrow.trianglehead.up.right|SFShoeArrowTriangleheadUpRight|Automotive,Objects & Tools|0
shoe.circle.fill|SFShoeCircleFill|Multicolor,Objects & Tools|0
shoe.circle|SFShoeCircle|Draw,Objects & Tools,Variable|0
shoe.fill|SFShoeFill|Objects & Tools|0
shoe|SFShoe|Objects & Tools|0
shoeprints.fill|SFShoeprintsFill|Human|0
shower.fill|SFShowerFill|Home,Objects & Tools,Variable|0
shower.handheld.fill|SFShowerHandheldFill|Home,Objects & Tools,Variable|0
shower.handheld|SFShowerHandheld|Home,Objects & Tools,Variable|0
shower.sidejet.fill|SFShowerSidejetFill|Home,Objects & Tools,Variable|0
shower.sidejet|SFShowerSidejet|Home,Objects & Tools,Variable|0
shower|SFShower|Home,Objects & Tools,Variable|0
shuffle.circle.fill|SFShuffleCircleFill|Arrows,Draw,Media,Multicolor|0
shuffle.circle|SFShuffleCircle|Arrows,Draw,Media,Variable|0
shuffle|SFShuffle|Arrows,Draw,Media|0
sidebar.leading|SFSidebarLeading||0
sidebar.left|SFSidebarLeft||0
sidebar.right|SFSidebarRight||0
sidebar.squares.leading|SFSidebarSquaresLeading||0
sidebar.squares.left|SFSidebarSquaresLeft||0
sidebar.squares.right|SFSidebarSquaresRight||0
sidebar.squares.trailing|SFSidebarSquaresTrailing||0
sidebar.trailing|SFSidebarTrailing||0
signature|SFSignature|Commerce,Draw,Editing,Text Formatting|0
signpost.and.arrowtriangle.up.circle.fill|SFSignpostAndArrowtriangleUpCircleFill|Multicolor,Objects & Tools|0
signpost.and.arrowtriangle.up.circle|SFSignpostAndArrowtriangleUpCircle|Draw,Objects & Tools,Variable|0
signpost.and.arrowtriangle.up.fill|SFSignpostAndArrowtriangleUpFill|Multicolor,Objects & Tools|0
signpost.and.arrowtriangle.up|SFSignpostAndArrowtriangleUp|Objects & Tools|0
signpost.left.circle.fill|SFSignpostLeftCircleFill|Multicolor,Objects & Tools|0
signpost.left.circle|SFSignpostLeftCircle|Draw,Objects & Tools,Variable|0
signpost.left.fill|SFSignpostLeftFill|Objects & Tools|0
signpost.left|SFSignpostLeft|Objects & Tools|0
signpost.right.and.left.circle.fill|SFSignpostRightAndLeftCircleFill|Multicolor,Objects & Tools|0
signpost.right.and.left.circle|SFSignpostRightAndLeftCircle|Draw,Objects & Tools,Variable|0
signpost.right.and.left.fill|SFSignpostRightAndLeftFill|Objects & Tools|0
signpost.right.and.left|SFSignpostRightAndLeft|Objects & Tools|0
signpost.right.circle.fill|SFSignpostRightCircleFill|Multicolor,Objects & Tools|0
signpost.right.circle|SFSignpostRightCircle|Draw,Objects & Tools,Variable|0
signpost.right.fill|SFSignpostRightFill|Objects & Tools|0
signpost.right|SFSignpostRight|Objects & Tools|0
simcard.2.fill|SFSimcard2Fill|Objects & Tools|0
simcard.2|SFSimcard2|Objects & Tools|0
simcard.fill|SFSimcardFill|Objects & Tools|0
simcard|SFSimcard|Objects & Tools|0
singaporedollarsign.arrow.trianglehead.counterclockwise.rotate.90|SFSingaporedollarsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
singaporedollarsign.bank.building.fill|SFSingaporedollarsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
singaporedollarsign.bank.building|SFSingaporedollarsignBankBuilding|Commerce,Objects & Tools|0
singaporedollarsign.circle.fill|SFSingaporedollarsignCircleFill|Commerce,Indices,Multicolor|0
singaporedollarsign.circle|SFSingaporedollarsignCircle|Commerce,Draw,Indices,Variable|0
singaporedollarsign.gauge.chart.lefthalf.righthalf|SFSingaporedollarsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
singaporedollarsign.gauge.chart.leftthird.topthird.rightthird|SFSingaporedollarsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
singaporedollarsign.ring.dashed|SFSingaporedollarsignRingDashed|Commerce,Home,Variable|0
singaporedollarsign.ring|SFSingaporedollarsignRing|Commerce,Draw,Home|0
singaporedollarsign.square.fill|SFSingaporedollarsignSquareFill|Commerce,Indices,Multicolor|0
singaporedollarsign.square|SFSingaporedollarsignSquare|Commerce,Draw,Indices|0
singaporedollarsign|SFSingaporedollarsign|Commerce,Indices|0
sink.fill|SFSinkFill|Home,Objects & Tools|0
sink|SFSink|Home,Objects & Tools|0
siri|SFSiri|Accessibility,Multicolor|1
skateboard.fill|SFSkateboardFill|Fitness,Objects & Tools|0
skateboard|SFSkateboard|Fitness,Objects & Tools|0
skew|SFSkew|Editing|0
skis.fill|SFSkisFill|Fitness,Objects & Tools|0
skis|SFSkis|Fitness,Objects & Tools|0
slash.circle.fill|SFSlashCircleFill|Multicolor|0
slash.circle|SFSlashCircle|Draw,Variable|0
sleep.circle.fill|SFSleepCircleFill|Multicolor|0
sleep.circle|SFSleepCircle|Draw,Variable|0
sleep|SFSleep||0
slider.horizontal.2.arrow.trianglehead.counterclockwise|SFSliderHorizontal2ArrowTriangleheadCounterclockwise|Arrows,Draw,Editing|0
slider.horizontal.2.rectangle.and.arrow.trianglehead.2.clockwise.rotate.90|SFSliderHorizontal2RectangleAndArrowTrianglehead2ClockwiseRotate90||0
slider.horizontal.2.square.badge.arrow.down|SFSliderHorizontal2SquareBadgeArrowDown|Editing,Multicolor|0
slider.horizontal.2.square.on.square|SFSliderHorizontal2SquareOnSquare|Editing|0
slider.horizontal.2.square|SFSliderHorizontal2Square|Editing|0
slider.horizontal.3|SFSliderHorizontal3|Editing|0
slider.horizontal.below.circle.lefthalf.filled.inverse|SFSliderHorizontalBelowCircleLefthalfFilledInverse|Editing|0
slider.horizontal.below.circle.lefthalf.filled|SFSliderHorizontalBelowCircleLefthalfFilled|Editing|0
slider.horizontal.below.circle.righthalf.filled.inverse|SFSliderHorizontalBelowCircleRighthalfFilledInverse|Editing|0
slider.horizontal.below.circle.righthalf.filled|SFSliderHorizontalBelowCircleRighthalfFilled|Editing|0
slider.horizontal.below.rectangle|SFSliderHorizontalBelowRectangle|Editing|0
slider.horizontal.below.square.and.square.filled|SFSliderHorizontalBelowSquareAndSquareFilled|Editing|0
slider.horizontal.below.square.filled.and.square|SFSliderHorizontalBelowSquareFilledAndSquare|Editing|0
slider.horizontal.below.sun.max|SFSliderHorizontalBelowSunMax|Editing|0
slider.horizontal.below.sun.min|SFSliderHorizontalBelowSunMin|Editing|0
slider.vertical.3|SFSliderVertical3|Editing|0
slowmo|SFSlowmo|Draw,Variable|0
smallcircle.circle.fill|SFSmallcircleCircleFill|Multicolor|0
smallcircle.circle|SFSmallcircleCircle|Draw,Variable|0
smallcircle.filled.circle.fill|SFSmallcircleFilledCircleFill|Accessibility,Multicolor|0
smallcircle.filled.circle|SFSmallcircleFilledCircle|Accessibility,Draw,Variable|0
smartphone|SFSmartphone|Devices|0
smoke.circle.fill|SFSmokeCircleFill|Multicolor,Nature,Weather|0
smoke.circle|SFSmokeCircle|Draw,Nature,Variable,Weather|0
smoke.fill|SFSmokeFill|Multicolor,Nature,Weather|0
smoke|SFSmoke|Nature,Weather|0
snowboard.fill|SFSnowboardFill|Fitness,Objects & Tools|0
snowboard|SFSnowboard|Fitness,Objects & Tools|0
snowflake.circle.fill|SFSnowflakeCircleFill|Automotive,Multicolor,Nature,Weather|0
snowflake.circle|SFSnowflakeCircle|Automotive,Draw,Nature,Variable,Weather|0
snowflake.road.lane.dashed|SFSnowflakeRoadLaneDashed|Automotive,Draw|0
snowflake.road.lane|SFSnowflakeRoadLane|Automotive,Draw|0
snowflake.slash|SFSnowflakeSlash|Automotive,Multicolor,Nature,Weather|0
snowflake|SFSnowflake|Automotive,Draw,Nature,Weather|0
soccerball.circle.fill.inverse|SFSoccerballCircleFillInverse|Fitness,Multicolor,Objects & Tools|0
soccerball.circle.fill|SFSoccerballCircleFill|Fitness,Multicolor,Objects & Tools|0
soccerball.circle.inverse|SFSoccerballCircleInverse|Fitness,Objects & Tools|0
soccerball.circle|SFSoccerballCircle|Draw,Fitness,Objects & Tools,Variable|0
soccerball.inverse|SFSoccerballInverse|Fitness,Objects & Tools|0
soccerball|SFSoccerball|Fitness,Objects & Tools|0
sofa.fill|SFSofaFill|Home,Objects & Tools|0
sofa|SFSofa|Home,Objects & Tools|0
sos.circle.fill|SFSosCircleFill|Connectivity,Multicolor|0
sos.circle|SFSosCircle|Connectivity,Draw,Variable|0
sos|SFSos|Connectivity|0
space|SFSpace|Draw,Keyboard|0
sparkle.magnifyingglass|SFSparkleMagnifyingglass|Objects & Tools|0
sparkle|SFSparkle|Multicolor|0
sparkle.text.clipboard.fill|SFSparkleTextClipboardFill|Health,Multicolor,Objects & Tools|0
sparkle.text.clipboard|SFSparkleTextClipboard|Health,Objects & Tools|0
sparkles.2|SFSparkles2||0
sparkles.rectangle.stack.fill|SFSparklesRectangleStackFill|Multicolor|0
sparkles.rectangle.stack|SFSparklesRectangleStack||0
sparkles.square.filled.on.square|SFSparklesSquareFilledOnSquare||0
sparkles|SFSparkles|Multicolor,Nature,Weather|0
sparkles.tv.fill|SFSparklesTvFill|Devices|0
sparkles.tv|SFSparklesTv|Devices|0
spatial.capture.fill|SFSpatialCaptureFill||1
spatial.capture.on.hexagon.fill|SFSpatialCaptureOnHexagonFill||1
spatial.capture.on.hexagon|SFSpatialCaptureOnHexagon||1
spatial.capture.slash.fill|SFSpatialCaptureSlashFill|Draw|1
spatial.capture.slash|SFSpatialCaptureSlash|Draw|1
spatial.capture|SFSpatialCapture||1
speaker.badge.exclamationmark.fill|SFSpeakerBadgeExclamationmarkFill|Multicolor,Objects & Tools|0
speaker.badge.exclamationmark|SFSpeakerBadgeExclamationmark|Multicolor,Objects & Tools|0
speaker.circle.fill|SFSpeakerCircleFill|Multicolor,Objects & Tools|0
speaker.circle|SFSpeakerCircle|Draw,Objects & Tools,Variable|0
speaker.fill|SFSpeakerFill|Objects & Tools|0
speaker.minus.fill|SFSpeakerMinusFill|Draw,Objects & Tools|0
speaker.minus|SFSpeakerMinus|Draw,Objects & Tools|0
speaker.plus.fill|SFSpeakerPlusFill|Draw,Objects & Tools|0
speaker.plus|SFSpeakerPlus|Draw,Objects & Tools|0
speaker.slash.circle.fill|SFSpeakerSlashCircleFill|Multicolor,Objects & Tools|0
speaker.slash.circle|SFSpeakerSlashCircle|Draw,Objects & Tools,Variable|0
speaker.slash.fill|SFSpeakerSlashFill|Draw,Objects & Tools|0
speaker.slash|SFSpeakerSlash|Draw,Objects & Tools|0
speaker.square.fill|SFSpeakerSquareFill|Multicolor,Objects & Tools|0
speaker.square|SFSpeakerSquare|Draw,Objects & Tools|0
speaker|SFSpeaker|Objects & Tools|0
speaker.trianglebadge.exclamationmark.fill|SFSpeakerTrianglebadgeExclamationmarkFill|Multicolor,Objects & Tools|0
speaker.trianglebadge.exclamationmark|SFSpeakerTrianglebadgeExclamationmark|Multicolor,Objects & Tools|0
speaker.wave.1.arrowtriangles.up.right.down.left|SFSpeakerWave1ArrowtrianglesUpRightDownLeft|Objects & Tools,Variable|0
speaker.wave.1.fill|SFSpeakerWave1Fill|Draw,Objects & Tools,Variable|0
speaker.wave.1|SFSpeakerWave1|Draw,Objects & Tools,Variable|0
speaker.wave.2.bubble.fill|SFSpeakerWave2BubbleFill|Communication,Multicolor,Variable|0
speaker.wave.2.bubble|SFSpeakerWave2Bubble|Communication,Variable|0
speaker.wave.2.circle.fill|SFSpeakerWave2CircleFill|Multicolor,Objects & Tools,Variable|0
speaker.wave.2.circle|SFSpeakerWave2Circle|Draw,Objects & Tools,Variable|0
speaker.wave.2.fill|SFSpeakerWave2Fill|Draw,Objects & Tools,Variable|0
speaker.wave.2|SFSpeakerWave2|Draw,Objects & Tools,Variable|0
speaker.wave.3.fill|SFSpeakerWave3Fill|Draw,Objects & Tools,Variable|0
speaker.wave.3|SFSpeakerWave3|Draw,Objects & Tools,Variable|0
speaker.zzz.fill|SFSpeakerZzzFill|Objects & Tools|0
speaker.zzz|SFSpeakerZzz|Objects & Tools|0
spigot.fill|SFSpigotFill|Home,Objects & Tools|0
spigot|SFSpigot|Home,Objects & Tools|0
spoon.serving|SFSpoonServing|Objects & Tools|0
sportscourt.circle.fill|SFSportscourtCircleFill|Fitness,Multicolor|0
sportscourt.circle|SFSportscourtCircle|Draw,Fitness,Variable|0
sportscourt.fill|SFSportscourtFill|Fitness|0
sportscourt|SFSportscourt|Fitness|0
sprinkler.and.droplets.fill|SFSprinklerAndDropletsFill|Home,Objects & Tools,Variable|0
sprinkler.and.droplets|SFSprinklerAndDroplets|Home,Objects & Tools,Variable|0
sprinkler.fill|SFSprinklerFill|Home,Objects & Tools|0
sprinkler|SFSprinkler|Home,Objects & Tools|0
square.2.layers.3d.bottom.filled|SFSquare2Layers3dBottomFilled|Camera & Photos|0
square.2.layers.3d.fill|SFSquare2Layers3dFill|Camera & Photos|0
square.2.layers.3d|SFSquare2Layers3d|Camera & Photos|0
square.2.layers.3d.top.filled|SFSquare2Layers3dTopFilled|Camera & Photos|0
square.3.layers.3d.bottom.filled|SFSquare3Layers3dBottomFilled|Camera & Photos|0
square.3.layers.3d.down.backward|SFSquare3Layers3dDownBackward|Camera & Photos,Variable|0
square.3.layers.3d.down.forward|SFSquare3Layers3dDownForward|Camera & Photos,Variable|0
square.3.layers.3d.down.left.slash|SFSquare3Layers3dDownLeftSlash|Camera & Photos|0
square.3.layers.3d.down.left|SFSquare3Layers3dDownLeft|Camera & Photos,Variable|0
square.3.layers.3d.down.right.slash|SFSquare3Layers3dDownRightSlash|Camera & Photos|0
square.3.layers.3d.down.right|SFSquare3Layers3dDownRight|Camera & Photos,Variable|0
square.3.layers.3d.middle.filled|SFSquare3Layers3dMiddleFilled|Camera & Photos|0
square.3.layers.3d.slash|SFSquare3Layers3dSlash|Camera & Photos|0
square.3.layers.3d|SFSquare3Layers3d|Camera & Photos,Variable|0
square.3.layers.3d.top.filled|SFSquare3Layers3dTopFilled|Camera & Photos|0
square.and.arrow.down.badge.checkmark.fill|SFSquareAndArrowDownBadgeCheckmarkFill|Multicolor|0
square.and.arrow.down.badge.checkmark|SFSquareAndArrowDownBadgeCheckmark|Multicolor|0
square.and.arrow.down.badge.clock.fill|SFSquareAndArrowDownBadgeClockFill|Multicolor|0
square.and.arrow.down.badge.clock|SFSquareAndArrowDownBadgeClock|Multicolor|0
square.and.arrow.down.badge.xmark.fill|SFSquareAndArrowDownBadgeXmarkFill|Multicolor|0
square.and.arrow.down.badge.xmark|SFSquareAndArrowDownBadgeXmark|Multicolor|0
square.and.arrow.down.fill|SFSquareAndArrowDownFill||0
square.and.arrow.down.on.square.fill|SFSquareAndArrowDownOnSquareFill||0
square.and.arrow.down.on.square|SFSquareAndArrowDownOnSquare||0
square.and.arrow.down|SFSquareAndArrowDown|Draw|0
square.and.arrow.up.badge.checkmark.fill|SFSquareAndArrowUpBadgeCheckmarkFill|Multicolor|0
square.and.arrow.up.badge.checkmark|SFSquareAndArrowUpBadgeCheckmark|Multicolor|0
square.and.arrow.up.badge.clock.fill|SFSquareAndArrowUpBadgeClockFill|Multicolor|0
square.and.arrow.up.badge.clock|SFSquareAndArrowUpBadgeClock|Multicolor|0
square.and.arrow.up.circle.fill|SFSquareAndArrowUpCircleFill|Multicolor|0
square.and.arrow.up.circle|SFSquareAndArrowUpCircle|Draw,Variable|0
square.and.arrow.up.fill|SFSquareAndArrowUpFill||0
square.and.arrow.up.on.square.fill|SFSquareAndArrowUpOnSquareFill||0
square.and.arrow.up.on.square|SFSquareAndArrowUpOnSquare||0
square.and.arrow.up|SFSquareAndArrowUp|Draw|0
square.and.arrow.up.trianglebadge.exclamationmark.fill|SFSquareAndArrowUpTrianglebadgeExclamationmarkFill|Multicolor|0
square.and.arrow.up.trianglebadge.exclamationmark|SFSquareAndArrowUpTrianglebadgeExclamationmark|Multicolor|0
square.and.at.rectangle.fill|SFSquareAndAtRectangleFill||0
square.and.at.rectangle|SFSquareAndAtRectangle||0
square.and.line.vertical.and.square.filled|SFSquareAndLineVerticalAndSquareFilled||0
square.and.line.vertical.and.square|SFSquareAndLineVerticalAndSquare||0
square.and.pencil.circle.fill|SFSquareAndPencilCircleFill|Editing,Multicolor,Objects & Tools|0
square.and.pencil.circle|SFSquareAndPencilCircle|Draw,Editing,Objects & Tools,Variable|0
square.and.pencil|SFSquareAndPencil|Draw,Editing,Objects & Tools|0
square.arrowtriangle.4.outward|SFSquareArrowtriangle4Outward||0
square.badge.plus.fill|SFSquareBadgePlusFill|Multicolor|0
square.badge.plus|SFSquareBadgePlus|Multicolor|0
square.bottomhalf.filled|SFSquareBottomhalfFilled||0
square.circle.fill|SFSquareCircleFill|Gaming,Multicolor|0
square.circle|SFSquareCircle|Draw,Gaming,Variable|0
square.dashed|SFSquareDashed|Editing|0
square.dotted|SFSquareDotted||0
square.fill.and.line.vertical.and.square.fill|SFSquareFillAndLineVerticalAndSquareFill||0
square.fill.on.circle.fill|SFSquareFillOnCircleFill||0
square.fill.on.square.fill|SFSquareFillOnSquareFill||0
square.fill|SFSquareFill|Shapes|0
square.fill.text.grid.1x2|SFSquareFillTextGrid1x2|Draw,Text Formatting|0
square.filled.and.line.vertical.and.square|SFSquareFilledAndLineVerticalAndSquare||0
square.filled.on.square|SFSquareFilledOnSquare||0
square.grid.2x2.fill|SFSquareGrid2x2Fill||0
square.grid.2x2|SFSquareGrid2x2||0
square.grid.3x1.below.line.grid.1x2.fill|SFSquareGrid3x1BelowLineGrid1x2Fill||0
square.grid.3x1.below.line.grid.1x2|SFSquareGrid3x1BelowLineGrid1x2||0
square.grid.3x1.folder.badge.plus|SFSquareGrid3x1FolderBadgePlus|Multicolor,Objects & Tools|0
square.grid.3x1.folder.fill.badge.plus|SFSquareGrid3x1FolderFillBadgePlus|Multicolor,Objects & Tools|0
square.grid.3x2.fill|SFSquareGrid3x2Fill||0
square.grid.3x2|SFSquareGrid3x2||0
square.grid.3x3.bottomleft.filled|SFSquareGrid3x3BottomleftFilled|Accessibility|0
square.grid.3x3.bottommiddle.filled|SFSquareGrid3x3BottommiddleFilled|Accessibility|0
square.grid.3x3.bottomright.filled|SFSquareGrid3x3BottomrightFilled|Accessibility|0
square.grid.3x3.fill|SFSquareGrid3x3Fill||0
square.grid.3x3.middle.filled|SFSquareGrid3x3MiddleFilled|Accessibility|0
square.grid.3x3.middleleft.filled|SFSquareGrid3x3MiddleleftFilled|Accessibility|0
square.grid.3x3.middleright.filled|SFSquareGrid3x3MiddlerightFilled|Accessibility|0
square.grid.3x3.square.badge.ellipsis|SFSquareGrid3x3SquareBadgeEllipsis|Multicolor|0
square.grid.3x3.square|SFSquareGrid3x3Square|Draw|0
square.grid.3x3|SFSquareGrid3x3||0
square.grid.3x3.topleft.filled|SFSquareGrid3x3TopleftFilled|Accessibility|0
square.grid.3x3.topmiddle.filled|SFSquareGrid3x3TopmiddleFilled|Accessibility|0
square.grid.3x3.topright.filled|SFSquareGrid3x3ToprightFilled|Accessibility|0
square.grid.4x3.fill|SFSquareGrid4x3Fill||0
square.lefthalf.filled|SFSquareLefthalfFilled||0
square.on.circle|SFSquareOnCircle||0
square.on.square.badge.person.crop.fill|SFSquareOnSquareBadgePersonCropFill|Human,Multicolor|0
square.on.square.badge.person.crop|SFSquareOnSquareBadgePersonCrop|Human,Multicolor|0
square.on.square.dashed|SFSquareOnSquareDashed||0
square.on.square.intersection.dashed|SFSquareOnSquareIntersectionDashed||0
square.on.square.squareshape.controlhandles|SFSquareOnSquareSquareshapeControlhandles||0
square.on.square|SFSquareOnSquare||0
square.resize.down|SFSquareResizeDown||0
square.resize|SFSquareResize||0
square.resize.up|SFSquareResizeUp||0
square.righthalf.filled|SFSquareRighthalfFilled||0
square.slash.fill|SFSquareSlashFill|Draw|0
square.slash|SFSquareSlash|Draw|0
square.split.1x2.fill|SFSquareSplit1x2Fill||0
square.split.1x2|SFSquareSplit1x2||0
square.split.2x1.fill|SFSquareSplit2x1Fill||0
square.split.2x1|SFSquareSplit2x1||0
square.split.2x2.fill|SFSquareSplit2x2Fill||0
square.split.2x2|SFSquareSplit2x2||0
square.split.bottomrightquarter.fill|SFSquareSplitBottomrightquarterFill|Home|0
square.split.bottomrightquarter|SFSquareSplitBottomrightquarter|Home|0
square.split.diagonal.2x2.fill|SFSquareSplitDiagonal2x2Fill||0
square.split.diagonal.2x2|SFSquareSplitDiagonal2x2||0
square.split.diagonal.fill|SFSquareSplitDiagonalFill||0
square.split.diagonal|SFSquareSplitDiagonal||0
square.stack.3d.down.forward.fill|SFSquareStack3dDownForwardFill|Variable|0
square.stack.3d.down.forward|SFSquareStack3dDownForward|Variable|0
square.stack.3d.down.right.fill|SFSquareStack3dDownRightFill|Variable|0
square.stack.3d.down.right|SFSquareStack3dDownRight|Variable|0
square.stack.3d.forward.dottedline.fill|SFSquareStack3dForwardDottedlineFill|Variable|0
square.stack.3d.forward.dottedline|SFSquareStack3dForwardDottedline|Variable|0
square.stack.3d.up.badge.automatic.fill|SFSquareStack3dUpBadgeAutomaticFill|Multicolor,Variable|0
square.stack.3d.up.badge.automatic|SFSquareStack3dUpBadgeAutomatic|Multicolor,Variable|0
square.stack.3d.up.fill|SFSquareStack3dUpFill|Variable|0
square.stack.3d.up.slash.fill|SFSquareStack3dUpSlashFill||0
square.stack.3d.up.slash|SFSquareStack3dUpSlash||0
square.stack.3d.up|SFSquareStack3dUp|Variable|0
square.stack.3d.up.trianglebadge.exclamationmark.fill|SFSquareStack3dUpTrianglebadgeExclamationmarkFill|Multicolor,Variable|0
square.stack.3d.up.trianglebadge.exclamationmark|SFSquareStack3dUpTrianglebadgeExclamationmark|Multicolor,Variable|0
square.stack.fill|SFSquareStackFill||0
square.stack|SFSquareStack||0
square|SFSquare|Draw,Shapes|0
square.text.square.fill|SFSquareTextSquareFill|Draw,Multicolor|0
square.text.square|SFSquareTextSquare|Draw|0
square.tophalf.filled|SFSquareTophalfFilled||0
squareroot|SFSquareroot|Draw,Math|0
squares.below.rectangle|SFSquaresBelowRectangle||0
squares.leading.rectangle.fill|SFSquaresLeadingRectangleFill||0
squares.leading.rectangle|SFSquaresLeadingRectangle||0
squareshape.controlhandles.on.squareshape.controlhandles|SFSquareshapeControlhandlesOnSquareshapeControlhandles||0
squareshape.dotted.squareshape|SFSquareshapeDottedSquareshape||0
squareshape.fill|SFSquareshapeFill||0
squareshape.split.2x2.dotted.inside.and.outside|SFSquareshapeSplit2x2DottedInsideAndOutside||0
squareshape.split.2x2.dotted.inside|SFSquareshapeSplit2x2DottedInside||0
squareshape.split.2x2.dotted.outside|SFSquareshapeSplit2x2DottedOutside||0
squareshape.split.2x2|SFSquareshapeSplit2x2||0
squareshape.split.3x3|SFSquareshapeSplit3x3||0
squareshape.squareshape.dotted|SFSquareshapeSquareshapeDotted||0
squareshape|SFSquareshape||0
stairs|SFStairs|Draw,Home|0
star.bubble.fill|SFStarBubbleFill|Communication,Multicolor|0
star.bubble|SFStarBubble|Communication|0
star.circle.fill|SFStarCircleFill|Multicolor|0
star.circle|SFStarCircle|Draw,Multicolor,Variable|0
star.fill|SFStarFill|Multicolor|0
star.hexagon.fill|SFStarHexagonFill|Multicolor,Shapes|0
star.hexagon|SFStarHexagon|Shapes|0
star.leadinghalf.filled|SFStarLeadinghalfFilled|Multicolor|0
star.rectangle.fill|SFStarRectangleFill|Multicolor|0
star.rectangle|SFStarRectangle|Draw|0
star.slash.fill|SFStarSlashFill|Draw,Multicolor|0
star.slash|SFStarSlash|Draw,Multicolor|0
star.square.fill|SFStarSquareFill|Multicolor|0
star.square.on.square.fill|SFStarSquareOnSquareFill||0
star.square.on.square|SFStarSquareOnSquare||0
star.square|SFStarSquare|Draw,Multicolor|0
star|SFStar|Multicolor|0
staroflife.circle.fill|SFStaroflifeCircleFill|Health,Multicolor|0
staroflife.circle|SFStaroflifeCircle|Draw,Health,Variable|0
staroflife.fill|SFStaroflifeFill|Health|0
staroflife.shield.fill|SFStaroflifeShieldFill|Multicolor,Objects & Tools|0
staroflife.shield|SFStaroflifeShield|Objects & Tools|0
staroflife|SFStaroflife|Health|0
steeringwheel.and.hands|SFSteeringwheelAndHands|Automotive|0
steeringwheel.and.heat.waves|SFSteeringwheelAndHeatWaves|Automotive|0
steeringwheel.and.key|SFSteeringwheelAndKey|Automotive|0
steeringwheel.and.liquid.wave|SFSteeringwheelAndLiquidWave|Automotive,Draw|0
steeringwheel.arrow.trianglehead.counterclockwise.and.clockwise|SFSteeringwheelArrowTriangleheadCounterclockwiseAndClockwise|Automotive,Draw|0
steeringwheel.arrowtriangle.left|SFSteeringwheelArrowtriangleLeft|Automotive|0
steeringwheel.arrowtriangle.right|SFSteeringwheelArrowtriangleRight|Automotive|0
steeringwheel.badge.exclamationmark|SFSteeringwheelBadgeExclamationmark|Automotive,Multicolor|0
steeringwheel.badge.lock|SFSteeringwheelBadgeLock|Automotive|0
steeringwheel.circle.fill|SFSteeringwheelCircleFill|Automotive,Multicolor|0
steeringwheel.circle|SFSteeringwheelCircle|Automotive,Draw,Variable|0
steeringwheel.exclamationmark|SFSteeringwheelExclamationmark|Automotive,Multicolor|0
steeringwheel.road.lane.dashed|SFSteeringwheelRoadLaneDashed|Automotive|0
steeringwheel.road.lane|SFSteeringwheelRoadLane|Automotive|0
steeringwheel.slash|SFSteeringwheelSlash|Automotive,Draw|0
steeringwheel|SFSteeringwheel|Automotive|0
sterlingsign.arrow.trianglehead.counterclockwise.rotate.90|SFSterlingsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
sterlingsign.bank.building.fill|SFSterlingsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
sterlingsign.bank.building|SFSterlingsignBankBuilding|Commerce,Objects & Tools|0
sterlingsign.circle.fill|SFSterlingsignCircleFill|Commerce,Indices,Multicolor|0
sterlingsign.circle|SFSterlingsignCircle|Commerce,Draw,Indices,Variable|0
sterlingsign.gauge.chart.lefthalf.righthalf|SFSterlingsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
sterlingsign.gauge.chart.leftthird.topthird.rightthird|SFSterlingsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
sterlingsign.ring.dashed|SFSterlingsignRingDashed|Commerce,Home,Variable|0
sterlingsign.ring|SFSterlingsignRing|Commerce,Draw,Home|0
sterlingsign.square.fill|SFSterlingsignSquareFill|Commerce,Indices,Multicolor|0
sterlingsign.square|SFSterlingsignSquare|Commerce,Draw,Indices|0
sterlingsign|SFSterlingsign|Commerce,Indices|0
stethoscope.circle.fill|SFStethoscopeCircleFill|Health,Multicolor,Objects & Tools|0
stethoscope.circle|SFStethoscopeCircle|Draw,Health,Objects & Tools,Variable|0
stethoscope|SFStethoscope|Health,Multicolor,Objects & Tools|0
stop.circle.fill|SFStopCircleFill|Media,Multicolor|0
stop.circle|SFStopCircle|Draw,Media,Variable|0
stop.fill|SFStopFill|Media|0
stop|SFStop|Media|0
stopwatch.fill|SFStopwatchFill|Multicolor,Objects & Tools,Time|0
stopwatch|SFStopwatch|Multicolor,Objects & Tools,Time|0
storefront.circle.fill|SFStorefrontCircleFill|Multicolor|0
storefront.circle|SFStorefrontCircle|Draw,Variable|0
storefront.fill|SFStorefrontFill||0
storefront|SFStorefront||0
stove.fill|SFStoveFill|Home,Objects & Tools|0
stove|SFStove|Home,Objects & Tools|0
strikethrough.double|SFStrikethroughDouble|Draw,Text Formatting|0
strikethrough|SFStrikethrough|Draw,Multicolor,Text Formatting|0
stroke.line.diagonal.slash|SFStrokeLineDiagonalSlash|Communication,Draw|0
stroke.line.diagonal|SFStrokeLineDiagonal|Communication|0
stroller.fill|SFStrollerFill|Objects & Tools|0
stroller|SFStroller|Objects & Tools|0
studentdesk|SFStudentdesk|Objects & Tools|0
suit.club.fill|SFSuitClubFill|Multicolor|0
suit.club|SFSuitClub|Multicolor|0
suit.diamond.fill|SFSuitDiamondFill|Multicolor|0
suit.diamond|SFSuitDiamond|Multicolor|0
suit.heart.fill|SFSuitHeartFill|Multicolor|0
suit.heart|SFSuitHeart|Multicolor|0
suit.spade.fill|SFSuitSpadeFill|Multicolor|0
suit.spade|SFSuitSpade|Multicolor|0
suitcase.cart.fill|SFSuitcaseCartFill|Objects & Tools|0
suitcase.cart|SFSuitcaseCart|Objects & Tools|0
suitcase.circle.fill|SFSuitcaseCircleFill|Multicolor|0
suitcase.circle|SFSuitcaseCircle|Draw,Variable|0
suitcase.fill|SFSuitcaseFill|Objects & Tools|0
suitcase.rolling.and.film.circle.fill|SFSuitcaseRollingAndFilmCircleFill|Multicolor,Objects & Tools|0
suitcase.rolling.and.film.circle|SFSuitcaseRollingAndFilmCircle|Draw,Objects & Tools,Variable|0
suitcase.rolling.and.film.fill|SFSuitcaseRollingAndFilmFill|Objects & Tools|0
suitcase.rolling.and.film|SFSuitcaseRollingAndFilm|Objects & Tools|0
suitcase.rolling.and.suitcase.circle.fill|SFSuitcaseRollingAndSuitcaseCircleFill|Multicolor,Objects & Tools|0
suitcase.rolling.and.suitcase.circle|SFSuitcaseRollingAndSuitcaseCircle|Draw,Objects & Tools,Variable|0
suitcase.rolling.and.suitcase.fill|SFSuitcaseRollingAndSuitcaseFill|Objects & Tools|0
suitcase.rolling.and.suitcase|SFSuitcaseRollingAndSuitcase|Objects & Tools|0
suitcase.rolling.circle.fill|SFSuitcaseRollingCircleFill|Multicolor,Objects & Tools|0
suitcase.rolling.circle|SFSuitcaseRollingCircle|Draw,Objects & Tools,Variable|0
suitcase.rolling.fill|SFSuitcaseRollingFill|Objects & Tools|0
suitcase.rolling|SFSuitcaseRolling|Objects & Tools|0
suitcase|SFSuitcase|Objects & Tools|0
sum|SFSum|Math|0
sun.dust.circle.fill|SFSunDustCircleFill|Multicolor,Nature,Weather|0
sun.dust.circle|SFSunDustCircle|Draw,Nature,Variable,Weather|0
sun.dust.fill|SFSunDustFill|Multicolor,Nature,Weather|0
sun.dust|SFSunDust|Nature,Weather|0
sun.haze.circle.fill|SFSunHazeCircleFill|Multicolor,Nature,Weather|0
sun.haze.circle|SFSunHazeCircle|Draw,Nature,Variable,Weather|0
sun.haze.fill|SFSunHazeFill|Multicolor,Nature,Weather|0
sun.haze|SFSunHaze|Nature,Weather|0
sun.horizon.circle.fill|SFSunHorizonCircleFill|Multicolor,Nature,Weather|0
sun.horizon.circle|SFSunHorizonCircle|Draw,Nature,Variable,Weather|0
sun.horizon.fill|SFSunHorizonFill|Multicolor,Nature,Weather|0
sun.horizon|SFSunHorizon|Nature,Weather|0
sun.lefthalf.filled|SFSunLefthalfFilled|Draw|0
sun.max.circle.fill|SFSunMaxCircleFill|Keyboard,Multicolor,Nature,Weather|0
sun.max.circle|SFSunMaxCircle|Draw,Keyboard,Nature,Variable,Weather|0
sun.max.fill|SFSunMaxFill|Draw,Keyboard,Multicolor,Nature,Weather|0
sun.max|SFSunMax|Draw,Keyboard,Nature,Weather|0
sun.max.trianglebadge.exclamationmark.fill|SFSunMaxTrianglebadgeExclamationmarkFill|Multicolor,Nature,Weather|0
sun.max.trianglebadge.exclamationmark|SFSunMaxTrianglebadgeExclamationmark|Multicolor,Nature,Weather|0
sun.min.fill|SFSunMinFill|Keyboard,Nature,Weather|0
sun.min|SFSunMin|Keyboard,Nature,Weather|0
sun.rain.circle.fill|SFSunRainCircleFill|Multicolor,Nature,Weather|0
sun.rain.circle|SFSunRainCircle|Draw,Nature,Variable,Weather|0
sun.rain.fill|SFSunRainFill|Multicolor,Nature,Weather|0
sun.rain|SFSunRain|Nature,Weather|0
sun.righthalf.filled|SFSunRighthalfFilled|Draw|0
sun.snow.circle.fill|SFSunSnowCircleFill|Multicolor,Nature,Weather|0
sun.snow.circle|SFSunSnowCircle|Draw,Nature,Variable,Weather|0
sun.snow.fill|SFSunSnowFill|Multicolor,Nature,Weather|0
sun.snow|SFSunSnow|Nature,Weather|0
sunglasses.fill|SFSunglassesFill|Objects & Tools|0
sunglasses|SFSunglasses|Multicolor,Objects & Tools|0
sunrise.circle.fill|SFSunriseCircleFill|Multicolor,Nature,Weather|0
sunrise.circle|SFSunriseCircle|Draw,Nature,Variable,Weather|0
sunrise.fill|SFSunriseFill|Multicolor,Nature,Weather|0
sunrise|SFSunrise|Nature,Weather|0
sunset.circle.fill|SFSunsetCircleFill|Multicolor,Nature,Weather|0
sunset.circle|SFSunsetCircle|Draw,Nature,Variable,Weather|0
sunset.fill|SFSunsetFill|Multicolor,Nature,Weather|0
sunset|SFSunset|Nature,Weather|0
surfboard.fill|SFSurfboardFill|Fitness,Objects & Tools|0
surfboard|SFSurfboard|Fitness,Objects & Tools|0
suspension.shock|SFSuspensionShock|Automotive|0
suv.side.air.circulate.fill|SFSuvSideAirCirculateFill|Automotive|0
suv.side.air.circulate|SFSuvSideAirCirculate|Automotive|0
suv.side.air.fresh.fill|SFSuvSideAirFreshFill|Automotive|0
suv.side.air.fresh|SFSuvSideAirFresh|Automotive|0
suv.side.and.exclamationmark.fill|SFSuvSideAndExclamationmarkFill|Automotive,Multicolor|0
suv.side.and.exclamationmark|SFSuvSideAndExclamationmark|Automotive,Multicolor|0
suv.side.arrow.left.and.right.fill|SFSuvSideArrowLeftAndRightFill|Automotive,Draw|0
suv.side.arrow.left.and.right|SFSuvSideArrowLeftAndRight|Automotive,Draw|0
suv.side.arrowtriangle.down.fill|SFSuvSideArrowtriangleDownFill|Automotive|0
suv.side.arrowtriangle.down|SFSuvSideArrowtriangleDown|Automotive|0
suv.side.arrowtriangle.up.arrowtriangle.down.fill|SFSuvSideArrowtriangleUpArrowtriangleDownFill|Automotive|0
suv.side.arrowtriangle.up.arrowtriangle.down|SFSuvSideArrowtriangleUpArrowtriangleDown|Automotive|0
suv.side.arrowtriangle.up.fill|SFSuvSideArrowtriangleUpFill|Automotive|0
suv.side.arrowtriangle.up|SFSuvSideArrowtriangleUp|Automotive|0
suv.side.fill|SFSuvSideFill|Automotive|0
suv.side.front.open.crop.fill|SFSuvSideFrontOpenCropFill|Automotive|0
suv.side.front.open.crop|SFSuvSideFrontOpenCrop|Automotive|0
suv.side.front.open.fill|SFSuvSideFrontOpenFill|Automotive,Multicolor|0
suv.side.front.open|SFSuvSideFrontOpen|Automotive,Multicolor|0
suv.side.hill.descent.control.fill|SFSuvSideHillDescentControlFill|Automotive,Draw|0
suv.side.hill.descent.control|SFSuvSideHillDescentControl|Automotive,Draw|0
suv.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle.fill|SFSuvSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangleFill|Automotive,Draw|0
suv.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle|SFSuvSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangle|Automotive,Draw|0
suv.side.hill.down.fill|SFSuvSideHillDownFill|Automotive,Draw|0
suv.side.hill.down|SFSuvSideHillDown|Automotive,Draw|0
suv.side.hill.up.fill|SFSuvSideHillUpFill|Automotive,Draw|0
suv.side.hill.up|SFSuvSideHillUp|Automotive,Draw|0
suv.side.lock.fill|SFSuvSideLockFill|Automotive|0
suv.side.lock.open.fill|SFSuvSideLockOpenFill|Automotive|0
suv.side.lock.open|SFSuvSideLockOpen|Automotive|0
suv.side.lock|SFSuvSideLock|Automotive|0
suv.side.rear.open.crop.fill|SFSuvSideRearOpenCropFill|Automotive|0
suv.side.rear.open.crop|SFSuvSideRearOpenCrop|Automotive|0
suv.side.rear.open.fill|SFSuvSideRearOpenFill|Automotive,Multicolor|0
suv.side.rear.open|SFSuvSideRearOpen|Automotive,Multicolor|0
suv.side.roof.cargo.carrier.fill|SFSuvSideRoofCargoCarrierFill|Automotive|0
suv.side.roof.cargo.carrier.slash.fill|SFSuvSideRoofCargoCarrierSlashFill|Automotive|0
suv.side.roof.cargo.carrier.slash|SFSuvSideRoofCargoCarrierSlash|Automotive|0
suv.side.roof.cargo.carrier|SFSuvSideRoofCargoCarrier|Automotive|0
suv.side|SFSuvSide|Automotive|0
swatchpalette.fill|SFSwatchpaletteFill|Objects & Tools|0
swatchpalette|SFSwatchpalette|Objects & Tools|0
swedishkronasign.arrow.trianglehead.counterclockwise.rotate.90|SFSwedishkronasignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
swedishkronasign.bank.building.fill|SFSwedishkronasignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
swedishkronasign.bank.building|SFSwedishkronasignBankBuilding|Commerce,Objects & Tools|0
swedishkronasign.circle.fill|SFSwedishkronasignCircleFill|Commerce,Indices,Multicolor|0
swedishkronasign.circle|SFSwedishkronasignCircle|Commerce,Draw,Indices,Variable|0
swedishkronasign.gauge.chart.lefthalf.righthalf|SFSwedishkronasignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
swedishkronasign.gauge.chart.leftthird.topthird.rightthird|SFSwedishkronasignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
swedishkronasign.ring.dashed|SFSwedishkronasignRingDashed|Commerce,Home,Variable|0
swedishkronasign.ring|SFSwedishkronasignRing|Commerce,Draw,Home|0
swedishkronasign.square.fill|SFSwedishkronasignSquareFill|Commerce,Indices,Multicolor|0
swedishkronasign.square|SFSwedishkronasignSquare|Commerce,Draw,Indices|0
swedishkronasign|SFSwedishkronasign|Commerce,Indices|0
swift|SFSwift||1
swiftdata|SFSwiftdata||1
swirl.circle.righthalf.filled.inverse|SFSwirlCircleRighthalfFilledInverse|Camera & Photos|0
swirl.circle.righthalf.filled|SFSwirlCircleRighthalfFilled|Camera & Photos|0
switch.2|SFSwitch2||0
switch.programmable.fill|SFSwitchProgrammableFill|Home|0
switch.programmable.square.fill|SFSwitchProgrammableSquareFill|Home,Multicolor|0
switch.programmable.square|SFSwitchProgrammableSquare|Draw,Home|0
switch.programmable|SFSwitchProgrammable|Home|0
syringe.fill|SFSyringeFill|Health,Objects & Tools|0
syringe|SFSyringe|Health,Objects & Tools|0
t.circle.fill|SFTCircleFill|Indices,Multicolor|0
t.circle|SFTCircle|Draw,Indices,Variable|0
t.square.fill|SFTSquareFill|Indices,Multicolor|0
t.square|SFTSquare|Draw,Indices|0
table.furniture.fill|SFTableFurnitureFill|Home,Objects & Tools|0
table.furniture|SFTableFurniture|Home,Objects & Tools|0
tablecells.badge.ellipsis|SFTablecellsBadgeEllipsis|Multicolor|0
tablecells.fill.badge.ellipsis|SFTablecellsFillBadgeEllipsis|Multicolor|0
tablecells.fill|SFTablecellsFill||0
tablecells|SFTablecells||0
tachometer|SFTachometer|Automotive,Draw|0
tag.circle.fill|SFTagCircleFill|Multicolor,Objects & Tools|0
tag.circle|SFTagCircle|Draw,Objects & Tools,Variable|0
tag.fill|SFTagFill|Objects & Tools|0
tag.slash.fill|SFTagSlashFill|Draw,Objects & Tools|0
tag.slash|SFTagSlash|Draw,Objects & Tools|0
tag.square.fill|SFTagSquareFill|Multicolor,Objects & Tools|0
tag.square|SFTagSquare|Draw,Objects & Tools|0
tag|SFTag|Objects & Tools|0
taillight.fog.fill|SFTaillightFogFill|Automotive,Multicolor|0
taillight.fog|SFTaillightFog|Automotive,Multicolor|0
takeoutbag.and.cup.and.straw.fill|SFTakeoutbagAndCupAndStrawFill|Objects & Tools|0
takeoutbag.and.cup.and.straw|SFTakeoutbagAndCupAndStraw|Objects & Tools|0
target|SFTarget|Draw,Variable|0
teddybear.fill|SFTeddybearFill|Objects & Tools|0
teddybear|SFTeddybear|Objects & Tools|0
teletype.answer.circle.fill|SFTeletypeAnswerCircleFill|Accessibility,Communication,Multicolor|1
teletype.answer.circle|SFTeletypeAnswerCircle|Accessibility,Communication,Draw,Multicolor,Variable|1
teletype.answer|SFTeletypeAnswer|Accessibility,Communication,Multicolor|1
teletype.circle.fill|SFTeletypeCircleFill|Accessibility,Communication,Multicolor|1
teletype.circle|SFTeletypeCircle|Accessibility,Communication,Draw,Multicolor,Variable|1
teletype|SFTeletype|Accessibility,Communication,Multicolor|1
tengesign.arrow.trianglehead.counterclockwise.rotate.90|SFTengesignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
tengesign.bank.building.fill|SFTengesignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
tengesign.bank.building|SFTengesignBankBuilding|Commerce,Objects & Tools|0
tengesign.circle.fill|SFTengesignCircleFill|Commerce,Indices,Multicolor|0
tengesign.circle|SFTengesignCircle|Commerce,Draw,Indices,Variable|0
tengesign.gauge.chart.lefthalf.righthalf|SFTengesignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
tengesign.gauge.chart.leftthird.topthird.rightthird|SFTengesignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
tengesign.ring.dashed|SFTengesignRingDashed|Commerce,Home,Variable|0
tengesign.ring|SFTengesignRing|Commerce,Draw,Home|0
tengesign.square.fill|SFTengesignSquareFill|Commerce,Indices,Multicolor|0
tengesign.square|SFTengesignSquare|Commerce,Draw,Indices|0
tengesign|SFTengesign|Commerce,Indices|0
tennis.racket.circle.fill|SFTennisRacketCircleFill|Fitness,Multicolor,Objects & Tools|0
tennis.racket.circle|SFTennisRacketCircle|Draw,Fitness,Objects & Tools,Variable|0
tennis.racket|SFTennisRacket|Fitness,Objects & Tools|0
tennisball.circle.fill|SFTennisballCircleFill|Fitness,Multicolor,Objects & Tools|0
tennisball.circle|SFTennisballCircle|Draw,Fitness,Objects & Tools,Variable|0
tennisball.fill|SFTennisballFill|Fitness,Objects & Tools|0
tennisball|SFTennisball|Fitness,Objects & Tools|0
tent.2.circle.fill|SFTent2CircleFill|Multicolor,Objects & Tools|0
tent.2.circle|SFTent2Circle|Draw,Objects & Tools,Variable|0
tent.2.fill|SFTent2Fill|Objects & Tools|0
tent.2|SFTent2|Objects & Tools|0
tent.circle.fill|SFTentCircleFill|Multicolor,Objects & Tools|0
tent.circle|SFTentCircle|Draw,Objects & Tools,Variable|0
tent.fill|SFTentFill|Objects & Tools|0
tent|SFTent|Objects & Tools|0
testtube.2|SFTesttube2|Multicolor,Objects & Tools|0
text.aligncenter|SFTextAligncenter|Draw,Text Formatting|0
text.alignleft|SFTextAlignleft|Draw,Text Formatting|0
text.alignright|SFTextAlignright|Draw,Text Formatting|0
text.and.command.macwindow|SFTextAndCommandMacwindow|Multicolor|0
text.append|SFTextAppend|Arrows,Draw,Media,Text Formatting|0
text.badge.checkmark|SFTextBadgeCheckmark|Draw,Multicolor|0
text.badge.minus|SFTextBadgeMinus|Draw,Multicolor|0
text.badge.plus|SFTextBadgePlus|Draw,Multicolor|0
text.badge.star|SFTextBadgeStar|Draw,Multicolor|0
text.badge.xmark|SFTextBadgeXmark|Draw,Multicolor|0
text.below.folder.fill|SFTextBelowFolderFill|Draw,Objects & Tools|0
text.below.folder|SFTextBelowFolder|Draw,Objects & Tools|0
text.below.photo.fill|SFTextBelowPhotoFill|Camera & Photos,Draw|0
text.below.photo|SFTextBelowPhoto|Camera & Photos,Draw|0
text.book.closed.fill|SFTextBookClosedFill|Multicolor,Objects & Tools|0
text.book.closed|SFTextBookClosed|Objects & Tools|0
text.bubble.badge.clock.fill|SFTextBubbleBadgeClockFill|Communication,Multicolor|0
text.bubble.badge.clock|SFTextBubbleBadgeClock|Communication,Multicolor|0
text.bubble.fill|SFTextBubbleFill|Communication,Draw,Multicolor|0
text.bubble|SFTextBubble|Communication,Draw|0
text.document.fill|SFTextDocumentFill|Draw,Multicolor,Objects & Tools|0
text.document|SFTextDocument|Draw,Multicolor,Objects & Tools|0
text.insert|SFTextInsert|Arrows,Draw,Media,Text Formatting|0
text.justify.leading|SFTextJustifyLeading|Draw,Text Formatting|0
text.justify.left|SFTextJustifyLeft|Draw,Text Formatting|0
text.justify.right|SFTextJustifyRight|Draw,Text Formatting|0
text.justify|SFTextJustify|Draw,Text Formatting|0
text.justify.trailing|SFTextJustifyTrailing|Draw,Text Formatting|0
text.line.2.summary.badge.xmark|SFTextLine2SummaryBadgeXmark|Multicolor,Text Formatting|1
text.line.2.summary|SFTextLine2Summary|Draw,Text Formatting|1
text.line.3.summary|SFTextLine3Summary|Draw,Text Formatting|1
text.line.first.and.arrowtriangle.forward|SFTextLineFirstAndArrowtriangleForward|Draw,Media|0
text.line.last.and.arrowtriangle.forward|SFTextLineLastAndArrowtriangleForward|Draw,Media|0
text.line.magnify|SFTextLineMagnify|Draw,Text Formatting|0
text.magnifyingglass|SFTextMagnifyingglass|Objects & Tools|0
text.pad.header.badge.clock|SFTextPadHeaderBadgeClock|Multicolor,Objects & Tools|0
text.pad.header.badge.plus|SFTextPadHeaderBadgePlus|Multicolor,Objects & Tools|0
text.pad.header|SFTextPadHeader|Draw,Objects & Tools|0
text.page.badge.magnifyingglass|SFTextPageBadgeMagnifyingglass|Objects & Tools|0
text.page.fill|SFTextPageFill|Draw,Multicolor|0
text.page.slash.fill|SFTextPageSlashFill||0
text.page.slash|SFTextPageSlash||0
text.page|SFTextPage|Draw|0
text.quote|SFTextQuote|Draw|0
text.rectangle.fill|SFTextRectangleFill|Multicolor|0
text.rectangle.page.fill|SFTextRectanglePageFill|Multicolor|0
text.rectangle.page|SFTextRectanglePage||0
text.rectangle|SFTextRectangle|Draw|0
text.redaction|SFTextRedaction|Draw,Text Formatting|0
text.square.filled|SFTextSquareFilled|Draw,Text Formatting|0
text.viewfinder|SFTextViewfinder|Draw|0
text.word.spacing|SFTextWordSpacing|Draw,Text Formatting|0
textformat.alt|SFTextformatAlt|Text Formatting|0
textformat.characters.arrow.left.and.right|SFTextformatCharactersArrowLeftAndRight|Draw,Text Formatting|0
textformat.characters.dottedunderline|SFTextformatCharactersDottedunderline|Multicolor,Text Formatting|0
textformat.characters|SFTextformatCharacters|Text Formatting|0
textformat.numbers|SFTextformatNumbers|Text Formatting|0
textformat.size.larger|SFTextformatSizeLarger|Accessibility,Text Formatting|0
textformat.size.smaller|SFTextformatSizeSmaller|Accessibility,Text Formatting|0
textformat.size|SFTextformatSize|Accessibility,Text Formatting|0
textformat.subscript|SFTextformatSubscript|Text Formatting|0
textformat.superscript|SFTextformatSuperscript|Text Formatting|0
textformat|SFTextformat|Text Formatting|0
theatermask.and.paintbrush.fill|SFTheatermaskAndPaintbrushFill|Objects & Tools|0
theatermask.and.paintbrush|SFTheatermaskAndPaintbrush|Objects & Tools|0
theatermasks.circle.fill|SFTheatermasksCircleFill|Multicolor,Objects & Tools|0
theatermasks.circle|SFTheatermasksCircle|Draw,Objects & Tools,Variable|0
theatermasks.fill|SFTheatermasksFill|Objects & Tools|0
theatermasks|SFTheatermasks|Objects & Tools|0
thermometer.and.ellipsis|SFThermometerAndEllipsis|Home,Variable|0
thermometer.and.liquid.waves.snowflake|SFThermometerAndLiquidWavesSnowflake|Automotive,Draw|0
thermometer.and.liquid.waves|SFThermometerAndLiquidWaves|Automotive|0
thermometer.and.liquid.waves.trianglebadge.exclamationmark|SFThermometerAndLiquidWavesTrianglebadgeExclamationmark|Automotive,Multicolor|0
thermometer.brakesignal|SFThermometerBrakesignal|Automotive,Multicolor|0
thermometer.gauge.open|SFThermometerGaugeOpen|Draw,Variable|0
thermometer.high|SFThermometerHigh|Multicolor,Variable,Weather|0
thermometer.low|SFThermometerLow|Draw,Multicolor,Variable,Weather|0
thermometer.medium.slash|SFThermometerMediumSlash|Variable,Weather|0
thermometer.medium|SFThermometerMedium|Draw,Multicolor,Variable,Weather|0
thermometer.snowflake.circle.fill|SFThermometerSnowflakeCircleFill|Multicolor,Nature,Weather|0
thermometer.snowflake.circle|SFThermometerSnowflakeCircle|Draw,Nature,Variable,Weather|0
thermometer.snowflake|SFThermometerSnowflake|Draw,Multicolor,Nature,Variable,Weather|0
thermometer.sun.circle.fill|SFThermometerSunCircleFill|Multicolor,Nature,Weather|0
thermometer.sun.circle|SFThermometerSunCircle|Draw,Nature,Variable,Weather|0
thermometer.sun.fill|SFThermometerSunFill|Draw,Multicolor,Nature,Variable,Weather|0
thermometer.sun|SFThermometerSun|Draw,Nature,Variable,Weather|0
thermometer.tirepressure|SFThermometerTirepressure|Automotive|0
thermometer.transmission|SFThermometerTransmission|Automotive|0
thermometer.variable.and.figure.circle.fill|SFThermometerVariableAndFigureCircleFill|Health,Multicolor,Nature,Weather|0
thermometer.variable.and.figure.circle|SFThermometerVariableAndFigureCircle|Draw,Health,Nature,Variable,Weather|0
thermometer.variable.and.figure|SFThermometerVariableAndFigure|Health,Multicolor,Nature,Weather|0
thermometer.variable.badge.clock|SFThermometerVariableBadgeClock|Health,Home,Multicolor,Nature,Weather|0
thermometer.variable.badge.play|SFThermometerVariableBadgePlay|Health,Home,Multicolor,Nature,Weather|0
thermometer.variable|SFThermometerVariable|Health,Multicolor,Nature,Weather|0
ticket.circle.fill|SFTicketCircleFill|Multicolor,Objects & Tools|0
ticket.circle|SFTicketCircle|Draw,Objects & Tools,Variable|0
ticket.fill|SFTicketFill|Objects & Tools|0
ticket|SFTicket|Objects & Tools|0
timelapse|SFTimelapse|Draw,Variable|0
timeline.selection|SFTimelineSelection|Editing|0
timer.circle.fill|SFTimerCircleFill|Multicolor,Objects & Tools,Time|0
timer.circle|SFTimerCircle|Draw,Objects & Tools,Time,Variable|0
timer.square|SFTimerSquare|Draw,Objects & Tools,Time|0
timer|SFTimer|Multicolor,Objects & Tools,Time|0
tire.badge.snowflake|SFTireBadgeSnowflake|Automotive|0
tire|SFTire|Automotive|0
tirepressure|SFTirepressure|Automotive|0
togglepower|SFTogglepower|Draw|0
toilet.circle.fill|SFToiletCircleFill|Home,Multicolor,Objects & Tools|0
toilet.circle|SFToiletCircle|Draw,Home,Objects & Tools,Variable|0
toilet.fill|SFToiletFill|Home,Objects & Tools|0
toilet|SFToilet|Home,Objects & Tools|0
tornado.circle.fill|SFTornadoCircleFill|Draw,Multicolor,Nature,Weather|0
tornado.circle|SFTornadoCircle|Draw,Nature,Variable,Weather|0
tornado|SFTornado|Draw,Multicolor,Nature,Weather|0
tortoise.circle.fill|SFTortoiseCircleFill|Accessibility,Multicolor,Nature|0
tortoise.circle|SFTortoiseCircle|Accessibility,Draw,Nature,Variable|0
tortoise.fill|SFTortoiseFill|Accessibility,Nature|0
tortoise|SFTortoise|Accessibility,Nature|0
torus|SFTorus||0
touchid|SFTouchid|Draw,Multicolor,Privacy & Security,Variable|1
tow.hitch.exclamationmark.fill|SFTowHitchExclamationmarkFill|Automotive|0
tow.hitch.exclamationmark|SFTowHitchExclamationmark|Automotive|0
tow.hitch.fill|SFTowHitchFill|Automotive|0
tow.hitch|SFTowHitch|Automotive|0
traction.control.tirepressure.exclamationmark|SFTractionControlTirepressureExclamationmark|Automotive|0
traction.control.tirepressure.slash|SFTractionControlTirepressureSlash|Automotive|0
traction.control.tirepressure|SFTractionControlTirepressure|Automotive|0
train.side.front.car|SFTrainSideFrontCar|Transportation|0
train.side.middle.car|SFTrainSideMiddleCar|Transportation|0
train.side.rear.car|SFTrainSideRearCar|Transportation|0
tram.card.fill|SFTramCardFill|Multicolor,Transportation|0
tram.card|SFTramCard|Transportation|0
tram.circle.fill|SFTramCircleFill|Multicolor,Transportation|0
tram.circle|SFTramCircle|Draw,Maps,Transportation,Variable|0
tram.fill|SFTramFill|Maps,Transportation|0
tram.fill.tunnel|SFTramFillTunnel|Transportation|0
tram|SFTram|Maps,Transportation|0
translate|SFTranslate|Communication|1
transmission|SFTransmission|Automotive|0
trapezoid.and.line.horizontal.fill|SFTrapezoidAndLineHorizontalFill|Camera & Photos,Editing|0
trapezoid.and.line.horizontal|SFTrapezoidAndLineHorizontal|Camera & Photos,Draw,Editing|0
trapezoid.and.line.vertical.fill|SFTrapezoidAndLineVerticalFill|Camera & Photos,Editing|0
trapezoid.and.line.vertical|SFTrapezoidAndLineVertical|Camera & Photos,Draw,Editing|0
trash.circle.fill|SFTrashCircleFill|Multicolor,Objects & Tools|0
trash.circle|SFTrashCircle|Draw,Multicolor,Objects & Tools,Variable|0
trash.fill|SFTrashFill|Multicolor,Objects & Tools|0
trash.slash.circle.fill|SFTrashSlashCircleFill|Multicolor,Objects & Tools|0
trash.slash.circle|SFTrashSlashCircle|Draw,Multicolor,Objects & Tools,Variable|0
trash.slash.fill|SFTrashSlashFill|Draw,Multicolor,Objects & Tools|0
trash.slash.square.fill|SFTrashSlashSquareFill|Multicolor,Objects & Tools|0
trash.slash.square|SFTrashSlashSquare|Draw,Multicolor,Objects & Tools|0
trash.slash|SFTrashSlash|Draw,Multicolor,Objects & Tools|0
trash.square.fill|SFTrashSquareFill|Multicolor,Objects & Tools|0
trash.square|SFTrashSquare|Draw,Multicolor,Objects & Tools|0
trash|SFTrash|Multicolor,Objects & Tools|0
tray.2.fill|SFTray2Fill|Objects & Tools|0
tray.2|SFTray2|Objects & Tools|0
tray.and.arrow.down.fill|SFTrayAndArrowDownFill|Draw,Objects & Tools|0
tray.and.arrow.down|SFTrayAndArrowDown|Draw,Objects & Tools|0
tray.and.arrow.up.fill|SFTrayAndArrowUpFill|Draw,Objects & Tools|0
tray.and.arrow.up|SFTrayAndArrowUp|Draw,Objects & Tools|0
tray.badge.fill|SFTrayBadgeFill|Multicolor,Objects & Tools|0
tray.badge|SFTrayBadge|Multicolor,Objects & Tools|0
tray.circle.fill|SFTrayCircleFill|Multicolor,Objects & Tools|0
tray.circle|SFTrayCircle|Draw,Objects & Tools,Variable|0
tray.fill|SFTrayFill|Objects & Tools|0
tray.full.fill|SFTrayFullFill|Objects & Tools|0
tray.full|SFTrayFull|Objects & Tools|0
tray|SFTray|Objects & Tools|0
tree.circle.fill|SFTreeCircleFill|Multicolor,Nature,Objects & Tools|0
tree.circle|SFTreeCircle|Draw,Multicolor,Nature,Objects & Tools,Variable|0
tree.fill|SFTreeFill|Multicolor,Nature,Objects & Tools|0
tree|SFTree|Multicolor,Nature,Objects & Tools|0
triangle.bottomhalf.filled|SFTriangleBottomhalfFilled||0
triangle.circle.fill|SFTriangleCircleFill|Gaming,Multicolor|0
triangle.circle|SFTriangleCircle|Draw,Gaming,Variable|0
triangle.fill|SFTriangleFill|Shapes|0
triangle.lefthalf.filled|SFTriangleLefthalfFilled||0
triangle.righthalf.filled|SFTriangleRighthalfFilled||0
triangle|SFTriangle|Shapes|0
triangle.tophalf.filled|SFTriangleTophalfFilled||0
triangleshape.fill|SFTriangleshapeFill|Shapes|0
triangleshape|SFTriangleshape|Shapes|0
trophy.circle.fill|SFTrophyCircleFill|Fitness,Multicolor,Objects & Tools|0
trophy.circle|SFTrophyCircle|Draw,Fitness,Objects & Tools,Variable|0
trophy.fill|SFTrophyFill|Fitness,Objects & Tools|0
trophy|SFTrophy|Fitness,Objects & Tools|0
tropicalstorm.circle.fill|SFTropicalstormCircleFill|Multicolor,Nature,Weather|0
tropicalstorm.circle|SFTropicalstormCircle|Draw,Nature,Variable,Weather|0
tropicalstorm|SFTropicalstorm|Multicolor,Nature,Weather|0
truck.box.badge.clock.fill|SFTruckBoxBadgeClockFill|Multicolor,Transportation|0
truck.box.badge.clock|SFTruckBoxBadgeClock|Multicolor,Transportation|0
truck.box.fill|SFTruckBoxFill|Transportation|0
truck.box|SFTruckBox|Transportation|0
truck.pickup.side.air.circulate.fill|SFTruckPickupSideAirCirculateFill|Automotive|0
truck.pickup.side.air.circulate|SFTruckPickupSideAirCirculate|Automotive|0
truck.pickup.side.air.fresh.fill|SFTruckPickupSideAirFreshFill|Automotive|0
truck.pickup.side.air.fresh|SFTruckPickupSideAirFresh|Automotive|0
truck.pickup.side.and.exclamationmark.fill|SFTruckPickupSideAndExclamationmarkFill|Automotive,Multicolor|0
truck.pickup.side.and.exclamationmark|SFTruckPickupSideAndExclamationmark|Automotive,Multicolor|0
truck.pickup.side.arrow.left.and.right.fill|SFTruckPickupSideArrowLeftAndRightFill|Automotive,Draw|0
truck.pickup.side.arrow.left.and.right|SFTruckPickupSideArrowLeftAndRight|Automotive,Draw|0
truck.pickup.side.arrowtriangle.down.fill|SFTruckPickupSideArrowtriangleDownFill|Automotive|0
truck.pickup.side.arrowtriangle.down|SFTruckPickupSideArrowtriangleDown|Automotive|0
truck.pickup.side.arrowtriangle.up.arrowtriangle.down.fill|SFTruckPickupSideArrowtriangleUpArrowtriangleDownFill|Automotive|0
truck.pickup.side.arrowtriangle.up.arrowtriangle.down|SFTruckPickupSideArrowtriangleUpArrowtriangleDown|Automotive|0
truck.pickup.side.arrowtriangle.up.fill|SFTruckPickupSideArrowtriangleUpFill|Automotive|0
truck.pickup.side.arrowtriangle.up|SFTruckPickupSideArrowtriangleUp|Automotive|0
truck.pickup.side.fill|SFTruckPickupSideFill|Automotive|0
truck.pickup.side.front.open.crop.fill|SFTruckPickupSideFrontOpenCropFill|Automotive|0
truck.pickup.side.front.open.crop|SFTruckPickupSideFrontOpenCrop|Automotive|0
truck.pickup.side.front.open.fill|SFTruckPickupSideFrontOpenFill|Automotive,Multicolor|0
truck.pickup.side.front.open|SFTruckPickupSideFrontOpen|Automotive,Multicolor|0
truck.pickup.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle.fill|SFTruckPickupSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangleFill|Automotive,Draw|0
truck.pickup.side.hill.down.and.gauge.open.with.lines.needle.25percent.and.arrowtriangle|SFTruckPickupSideHillDownAndGaugeOpenWithLinesNeedle25percentAndArrowtriangle|Automotive,Draw|0
truck.pickup.side.hill.down.fill|SFTruckPickupSideHillDownFill|Automotive,Draw|0
truck.pickup.side.hill.down|SFTruckPickupSideHillDown|Automotive,Draw|0
truck.pickup.side.hill.up.fill|SFTruckPickupSideHillUpFill|Automotive,Draw|0
truck.pickup.side.hill.up|SFTruckPickupSideHillUp|Automotive,Draw|0
truck.pickup.side.lock.fill|SFTruckPickupSideLockFill|Automotive|0
truck.pickup.side.lock.open.fill|SFTruckPickupSideLockOpenFill|Automotive|0
truck.pickup.side.lock.open|SFTruckPickupSideLockOpen|Automotive|0
truck.pickup.side.lock|SFTruckPickupSideLock|Automotive|0
truck.pickup.side|SFTruckPickupSide|Automotive|0
truck.side.hill.descent.control.fill|SFTruckSideHillDescentControlFill|Automotive,Draw|0
truck.side.hill.descent.control|SFTruckSideHillDescentControl|Automotive,Draw|0
truck.side.roof.cargo.carrier.fill|SFTruckSideRoofCargoCarrierFill|Automotive|0
truck.side.roof.cargo.carrier.slash.fill|SFTruckSideRoofCargoCarrierSlashFill|Automotive|0
truck.side.roof.cargo.carrier.slash|SFTruckSideRoofCargoCarrierSlash|Automotive|0
truck.side.roof.cargo.carrier|SFTruckSideRoofCargoCarrier|Automotive|0
tsa.circle.fill|SFTsaCircleFill|Automotive,Multicolor|0
tsa.circle|SFTsaCircle|Automotive,Draw,Variable|0
tsa.slash|SFTsaSlash|Automotive,Draw|0
tsa|SFTsa|Automotive|0
tshirt.circle.fill|SFTshirtCircleFill|Multicolor,Objects & Tools|0
tshirt.circle|SFTshirtCircle|Draw,Objects & Tools,Variable|0
tshirt.fill|SFTshirtFill|Objects & Tools|0
tshirt|SFTshirt|Objects & Tools|0
tugriksign.arrow.trianglehead.counterclockwise.rotate.90|SFTugriksignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
tugriksign.bank.building.fill|SFTugriksignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
tugriksign.bank.building|SFTugriksignBankBuilding|Commerce,Objects & Tools|0
tugriksign.circle.fill|SFTugriksignCircleFill|Commerce,Indices,Multicolor|0
tugriksign.circle|SFTugriksignCircle|Commerce,Draw,Indices,Variable|0
tugriksign.gauge.chart.lefthalf.righthalf|SFTugriksignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
tugriksign.gauge.chart.leftthird.topthird.rightthird|SFTugriksignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
tugriksign.ring.dashed|SFTugriksignRingDashed|Commerce,Home,Variable|0
tugriksign.ring|SFTugriksignRing|Commerce,Draw,Home|0
tugriksign.square.fill|SFTugriksignSquareFill|Commerce,Indices,Multicolor|0
tugriksign.square|SFTugriksignSquare|Commerce,Draw,Indices|0
tugriksign|SFTugriksign|Commerce,Indices|0
tuningfork|SFTuningfork|Objects & Tools|0
turkishlirasign.arrow.trianglehead.counterclockwise.rotate.90|SFTurkishlirasignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
turkishlirasign.bank.building.fill|SFTurkishlirasignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
turkishlirasign.bank.building|SFTurkishlirasignBankBuilding|Commerce,Objects & Tools|0
turkishlirasign.circle.fill|SFTurkishlirasignCircleFill|Commerce,Indices,Multicolor|0
turkishlirasign.circle|SFTurkishlirasignCircle|Commerce,Draw,Indices,Variable|0
turkishlirasign.gauge.chart.lefthalf.righthalf|SFTurkishlirasignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
turkishlirasign.gauge.chart.leftthird.topthird.rightthird|SFTurkishlirasignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
turkishlirasign.ring.dashed|SFTurkishlirasignRingDashed|Commerce,Home,Variable|0
turkishlirasign.ring|SFTurkishlirasignRing|Commerce,Draw,Home|0
turkishlirasign.square.fill|SFTurkishlirasignSquareFill|Commerce,Indices,Multicolor|0
turkishlirasign.square|SFTurkishlirasignSquare|Commerce,Draw,Indices|0
turkishlirasign|SFTurkishlirasign|Commerce,Indices|0
tv.and.hifispeaker.fill|SFTvAndHifispeakerFill|Devices|0
tv.and.mediabox.fill|SFTvAndMediaboxFill|Devices|0
tv.and.mediabox|SFTvAndMediabox|Devices|0
tv.badge.wifi.fill|SFTvBadgeWifiFill|Devices,Multicolor,Variable|0
tv.badge.wifi|SFTvBadgeWifi|Devices,Multicolor,Variable|0
tv.circle.fill|SFTvCircleFill|Devices,Multicolor|0
tv.circle|SFTvCircle|Devices,Draw,Variable|0
tv.fill|SFTvFill|Devices|0
tv.slash.fill|SFTvSlashFill|Devices,Draw|0
tv.slash|SFTvSlash|Devices|0
tv|SFTv|Devices|0
u.circle.fill|SFUCircleFill|Indices,Multicolor|0
u.circle|SFUCircle|Draw,Indices,Variable|0
u.square.fill|SFUSquareFill|Indices,Multicolor|0
u.square|SFUSquare|Draw,Indices|0
uiwindow.split.2x1|SFUiwindowSplit2x1||0
umbrella.circle.fill|SFUmbrellaCircleFill|Multicolor,Objects & Tools|0
umbrella.circle|SFUmbrellaCircle|Draw,Objects & Tools,Variable|0
umbrella.fill|SFUmbrellaFill|Objects & Tools|0
umbrella.gauge.open|SFUmbrellaGaugeOpen|Draw,Variable|0
umbrella.percent.fill|SFUmbrellaPercentFill|Objects & Tools|0
umbrella.percent|SFUmbrellaPercent|Objects & Tools|0
umbrella.sensor.tag.radiowaves.left.and.right.fill|SFUmbrellaSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
umbrella.sensor.tag.radiowaves.left.and.right|SFUmbrellaSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
umbrella|SFUmbrella|Objects & Tools|0
underline.double|SFUnderlineDouble|Draw,Text Formatting|0
underline|SFUnderline|Draw,Multicolor,Text Formatting|0
v.circle.fill|SFVCircleFill|Indices,Multicolor|0
v.circle|SFVCircle|Draw,Indices,Variable|0
v.square.fill|SFVSquareFill|Indices,Multicolor|0
v.square|SFVSquare|Draw,Indices|0
vent.heat.waves.upward|SFVentHeatWavesUpward|Arrows,Automotive|0
vial.viewfinder|SFVialViewfinder|Health,Objects & Tools|0
video.badge.checkmark|SFVideoBadgeCheckmark|Communication,Multicolor|1
video.badge.ellipsis|SFVideoBadgeEllipsis|Communication,Multicolor|1
video.badge.plus|SFVideoBadgePlus|Communication,Multicolor|1
video.badge.waveform.fill|SFVideoBadgeWaveformFill|Communication,Draw,Variable|1
video.badge.waveform|SFVideoBadgeWaveform|Communication,Draw,Variable|1
video.bubble.fill|SFVideoBubbleFill|Communication,Multicolor|1
video.bubble|SFVideoBubble|Communication|1
video.circle.fill|SFVideoCircleFill|Communication,Multicolor|1
video.circle|SFVideoCircle|Communication,Draw,Multicolor,Variable|1
video.doorbell.fill|SFVideoDoorbellFill|Home,Objects & Tools|0
video.doorbell|SFVideoDoorbell|Home,Objects & Tools|0
video.fill.badge.checkmark|SFVideoFillBadgeCheckmark|Communication,Multicolor|1
video.fill.badge.ellipsis|SFVideoFillBadgeEllipsis|Communication,Multicolor|1
video.fill.badge.plus|SFVideoFillBadgePlus|Communication,Multicolor|1
video.fill|SFVideoFill|Communication,Multicolor|1
video.slash.circle.fill|SFVideoSlashCircleFill|Communication,Multicolor|1
video.slash.circle|SFVideoSlashCircle|Communication,Draw,Multicolor,Variable|1
video.slash.fill|SFVideoSlashFill|Communication,Draw,Multicolor|1
video.slash|SFVideoSlash|Communication,Draw,Multicolor|1
video.square.fill|SFVideoSquareFill|Communication,Multicolor|1
video.square|SFVideoSquare|Communication,Draw,Multicolor|1
video|SFVideo|Communication,Multicolor|1
videoprojector.fill|SFVideoprojectorFill|Home,Objects & Tools|0
videoprojector|SFVideoprojector|Home,Objects & Tools|0
view.2d|SFView2d||0
view.3d|SFView3d||0
viewfinder.circle.fill|SFViewfinderCircleFill|Multicolor|0
viewfinder.circle|SFViewfinderCircle|Draw,Variable|0
viewfinder.rectangular|SFViewfinderRectangular||0
viewfinder|SFViewfinder|Shapes|0
viewfinder.trianglebadge.exclamationmark|SFViewfinderTrianglebadgeExclamationmark|Multicolor|0
vision.pro.and.arrow.forward.fill|SFVisionProAndArrowForwardFill|Devices|1
vision.pro.and.arrow.forward|SFVisionProAndArrowForward|Devices,Draw|1
vision.pro.badge.checkmark.fill|SFVisionProBadgeCheckmarkFill|Devices,Multicolor|1
vision.pro.badge.checkmark|SFVisionProBadgeCheckmark|Devices,Multicolor|1
vision.pro.badge.exclamationmark.fill|SFVisionProBadgeExclamationmarkFill|Devices,Multicolor|1
vision.pro.badge.exclamationmark|SFVisionProBadgeExclamationmark|Devices,Multicolor|1
vision.pro.badge.play.fill|SFVisionProBadgePlayFill|Devices,Multicolor|1
vision.pro.badge.play|SFVisionProBadgePlay|Devices,Multicolor|1
vision.pro.circle.fill|SFVisionProCircleFill|Devices,Multicolor|1
vision.pro.circle|SFVisionProCircle|Devices,Draw,Variable|1
vision.pro.fill|SFVisionProFill|Devices|1
vision.pro.slash.circle.fill|SFVisionProSlashCircleFill|Devices,Multicolor|1
vision.pro.slash.circle|SFVisionProSlashCircle|Devices,Draw,Variable|1
vision.pro.slash.fill|SFVisionProSlashFill|Devices,Draw|1
vision.pro.slash|SFVisionProSlash|Devices|1
vision.pro|SFVisionPro|Devices|1
vision.pro.trianglebadge.exclamationmark.fill|SFVisionProTrianglebadgeExclamationmarkFill|Devices,Multicolor|1
vision.pro.trianglebadge.exclamationmark|SFVisionProTrianglebadgeExclamationmark|Devices,Multicolor|1
voiceover|SFVoiceover|Accessibility,Human|1
volleyball.circle.fill|SFVolleyballCircleFill|Fitness,Multicolor,Objects & Tools|0
volleyball.circle|SFVolleyballCircle|Draw,Fitness,Objects & Tools,Variable|0
volleyball.fill|SFVolleyballFill|Fitness,Objects & Tools|0
volleyball|SFVolleyball|Fitness,Objects & Tools|0
w.circle.fill|SFWCircleFill|Indices,Multicolor|0
w.circle|SFWCircle|Draw,Indices,Variable|0
w.square.fill|SFWSquareFill|Indices,Multicolor|0
w.square|SFWSquare|Draw,Indices|0
wake.circle.fill|SFWakeCircleFill|Multicolor|0
wake.circle|SFWakeCircle|Draw,Variable|0
wake|SFWake||0
wallet.bifold.fill|SFWalletBifoldFill|Objects & Tools|0
wallet.bifold|SFWalletBifold|Objects & Tools|0
wallet.pass.fill|SFWalletPassFill|Draw,Multicolor,Objects & Tools|0
wallet.pass|SFWalletPass|Draw,Objects & Tools|0
wallet.sensor.tag.radiowaves.left.and.right.fill|SFWalletSensorTagRadiowavesLeftAndRightFill|Devices,Draw,Multicolor,Objects & Tools,Variable|0
wallet.sensor.tag.radiowaves.left.and.right|SFWalletSensorTagRadiowavesLeftAndRight|Devices,Draw,Objects & Tools,Variable|0
wand.and.outline.inverse|SFWandAndOutlineInverse|Draw,Editing,Objects & Tools|0
wand.and.outline|SFWandAndOutline|Draw,Editing,Objects & Tools|0
wand.and.rays.inverse|SFWandAndRaysInverse|Draw,Editing,Objects & Tools,Variable|0
wand.and.rays|SFWandAndRays|Draw,Editing,Objects & Tools,Variable|0
wand.and.sparkles.inverse|SFWandAndSparklesInverse|Editing,Objects & Tools|0
wand.and.sparkles|SFWandAndSparkles|Editing,Objects & Tools|0
warninglight.fill|SFWarninglightFill|Automotive,Draw|0
warninglight|SFWarninglight|Automotive,Draw|0
washer.circle.fill|SFWasherCircleFill|Home,Multicolor,Objects & Tools|0
washer.circle|SFWasherCircle|Draw,Home,Objects & Tools,Variable|0
washer.fill|SFWasherFill|Home,Objects & Tools|0
washer|SFWasher|Home,Objects & Tools|0
watch.analog|SFWatchAnalog|Objects & Tools|0
watchface.applewatch.case|SFWatchfaceApplewatchCase|Devices|1
water.waves.and.arrow.trianglehead.down|SFWaterWavesAndArrowTriangleheadDown|Draw,Fitness,Nature,Variable|0
water.waves.and.arrow.trianglehead.down.trianglebadge.exclamationmark|SFWaterWavesAndArrowTriangleheadDownTrianglebadgeExclamationmark|Fitness,Multicolor,Nature,Variable|0
water.waves.and.arrow.trianglehead.up|SFWaterWavesAndArrowTriangleheadUp|Draw,Fitness,Nature,Variable|0
water.waves.slash|SFWaterWavesSlash|Fitness,Nature|0
water.waves|SFWaterWaves|Draw,Fitness,Nature,Variable|0
waterbottle.fill|SFWaterbottleFill|Objects & Tools|0
waterbottle|SFWaterbottle|Objects & Tools|0
wave.3.backward.circle.fill|SFWave3BackwardCircleFill|Connectivity,Draw,Multicolor,Variable|0
wave.3.backward.circle|SFWave3BackwardCircle|Connectivity,Draw,Variable|0
wave.3.backward|SFWave3Backward|Connectivity,Draw,Variable|0
wave.3.down.car.side.fill|SFWave3DownCarSideFill|Automotive,Multicolor,Variable|0
wave.3.down.car.side|SFWave3DownCarSide|Automotive,Variable|0
wave.3.down.circle.fill|SFWave3DownCircleFill|Connectivity,Draw,Multicolor,Variable|0
wave.3.down.circle|SFWave3DownCircle|Connectivity,Draw,Variable|0
wave.3.down.convertible.side.fill|SFWave3DownConvertibleSideFill|Automotive,Multicolor,Variable|0
wave.3.down.convertible.side|SFWave3DownConvertibleSide|Automotive,Variable|0
wave.3.down.pickup.side.fill|SFWave3DownPickupSideFill|Automotive,Multicolor,Variable|0
wave.3.down.pickup.side|SFWave3DownPickupSide|Automotive,Variable|0
wave.3.down.suv.side.fill|SFWave3DownSuvSideFill|Automotive,Multicolor,Variable|0
wave.3.down.suv.side|SFWave3DownSuvSide|Automotive,Variable|0
wave.3.down|SFWave3Down|Connectivity,Draw,Variable|0
wave.3.forward.circle.fill|SFWave3ForwardCircleFill|Connectivity,Draw,Multicolor,Variable|0
wave.3.forward.circle|SFWave3ForwardCircle|Connectivity,Draw,Variable|0
wave.3.forward|SFWave3Forward|Connectivity,Draw,Variable|0
wave.3.left.circle.fill|SFWave3LeftCircleFill|Connectivity,Draw,Multicolor,Variable|0
wave.3.left.circle|SFWave3LeftCircle|Connectivity,Draw,Variable|0
wave.3.left|SFWave3Left|Connectivity,Draw,Variable|0
wave.3.right.circle.fill|SFWave3RightCircleFill|Connectivity,Draw,Multicolor,Variable|0
wave.3.right.circle|SFWave3RightCircle|Connectivity,Draw,Variable|0
wave.3.right|SFWave3Right|Connectivity,Draw,Variable|0
wave.3.up.circle.fill|SFWave3UpCircleFill|Connectivity,Draw,Multicolor,Variable|0
wave.3.up.circle|SFWave3UpCircle|Connectivity,Draw,Variable|0
wave.3.up|SFWave3Up|Connectivity,Draw,Variable|0
waveform.and.person.filled|SFWaveformAndPersonFilled|Communication,Human,Variable|0
waveform.badge.checkmark|SFWaveformBadgeCheckmark|Communication,Multicolor,Variable|0
waveform.badge.exclamationmark|SFWaveformBadgeExclamationmark|Communication,Multicolor,Variable|0
waveform.badge.magnifyingglass|SFWaveformBadgeMagnifyingglass|Accessibility,Communication,Variable|0
waveform.badge.microphone|SFWaveformBadgeMicrophone|Communication,Variable|0
waveform.badge.minus|SFWaveformBadgeMinus|Communication,Multicolor,Variable|0
waveform.badge.plus|SFWaveformBadgePlus|Communication,Multicolor,Variable|0
waveform.badge.xmark|SFWaveformBadgeXmark|Communication,Multicolor,Variable|0
waveform.circle.fill|SFWaveformCircleFill|Communication,Draw,Maps,Multicolor,Variable|0
waveform.circle|SFWaveformCircle|Communication,Draw,Maps,Variable|0
waveform.low|SFWaveformLow|Communication,Draw,Maps,Variable|0
waveform.mid|SFWaveformMid|Communication,Draw,Maps,Variable|0
waveform.path.badge.minus|SFWaveformPathBadgeMinus|Multicolor|0
waveform.path.badge.plus|SFWaveformPathBadgePlus|Multicolor|0
waveform.path.ecg.magnifyingglass|SFWaveformPathEcgMagnifyingglass|Objects & Tools|0
waveform.path.ecg.rectangle.fill|SFWaveformPathEcgRectangleFill|Health|0
waveform.path.ecg.rectangle|SFWaveformPathEcgRectangle|Health,Multicolor|0
waveform.path.ecg|SFWaveformPathEcg|Draw,Health|0
waveform.path.ecg.text.clipboard.fill|SFWaveformPathEcgTextClipboardFill|Health,Objects & Tools|0
waveform.path.ecg.text.clipboard|SFWaveformPathEcgTextClipboard|Health,Objects & Tools|0
waveform.path.ecg.text.page.fill|SFWaveformPathEcgTextPageFill||0
waveform.path.ecg.text.page|SFWaveformPathEcgTextPage||0
waveform.path.ecg.text|SFWaveformPathEcgText|Draw|0
waveform.path|SFWaveformPath||0
waveform.slash|SFWaveformSlash|Communication,Variable|0
waveform|SFWaveform|Communication,Draw,Maps,Variable|0
web.camera.fill|SFWebCameraFill|Home,Objects & Tools|0
web.camera|SFWebCamera|Home,Objects & Tools|0
wheelchair|SFWheelchair|Accessibility,Human|0
widget.extralarge.badge.plus|SFWidgetExtralargeBadgePlus|Multicolor|0
widget.extralarge|SFWidgetExtralarge||0
widget.large.badge.plus|SFWidgetLargeBadgePlus|Multicolor|0
widget.large|SFWidgetLarge||0
widget.medium.badge.plus|SFWidgetMediumBadgePlus|Multicolor|0
widget.medium|SFWidgetMedium||0
widget.small.badge.plus|SFWidgetSmallBadgePlus|Multicolor|0
widget.small|SFWidgetSmall||0
wifi.badge.lock|SFWifiBadgeLock|Connectivity,Variable|0
wifi.circle.fill|SFWifiCircleFill|Connectivity,Multicolor,Variable|0
wifi.circle|SFWifiCircle|Connectivity,Draw,Multicolor,Variable|0
wifi.exclamationmark.circle.fill|SFWifiExclamationmarkCircleFill|Connectivity,Multicolor|0
wifi.exclamationmark.circle|SFWifiExclamationmarkCircle|Connectivity,Draw,Variable|0
wifi.exclamationmark|SFWifiExclamationmark|Connectivity|0
wifi.router.fill|SFWifiRouterFill|Home,Objects & Tools,Variable|0
wifi.router|SFWifiRouter|Home,Objects & Tools,Variable|0
wifi.slash|SFWifiSlash|Connectivity,Multicolor|0
wifi.square.fill|SFWifiSquareFill|Connectivity,Multicolor,Variable|0
wifi.square|SFWifiSquare|Connectivity,Draw,Multicolor,Variable|0
wifi|SFWifi|Connectivity,Multicolor,Variable|0
wind.circle.fill|SFWindCircleFill|Draw,Multicolor,Nature,Weather|0
wind.circle|SFWindCircle|Draw,Nature,Variable,Weather|0
wind.snow.circle.fill|SFWindSnowCircleFill|Draw,Multicolor,Nature,Weather|0
wind.snow.circle|SFWindSnowCircle|Draw,Nature,Variable,Weather|0
wind.snow|SFWindSnow|Multicolor,Nature,Weather|0
wind|SFWind|Draw,Nature,Weather|0
window.awning.closed|SFWindowAwningClosed|Home|0
window.awning|SFWindowAwning|Home|0
window.casement.closed|SFWindowCasementClosed|Home|0
window.casement|SFWindowCasement|Home|0
window.ceiling.closed|SFWindowCeilingClosed|Home|0
window.ceiling|SFWindowCeiling|Home|0
window.horizontal.closed|SFWindowHorizontalClosed|Home|0
window.horizontal|SFWindowHorizontal|Home|0
window.shade.closed|SFWindowShadeClosed|Home|0
window.shade.open|SFWindowShadeOpen|Home|0
window.vertical.closed|SFWindowVerticalClosed|Home|0
window.vertical.open|SFWindowVerticalOpen|Home|0
windshield.front.and.fluid.and.spray|SFWindshieldFrontAndFluidAndSpray|Automotive|0
windshield.front.and.heat.waves|SFWindshieldFrontAndHeatWaves|Automotive|0
windshield.front.and.spray|SFWindshieldFrontAndSpray|Automotive|0
windshield.front.and.wiper.and.drop|SFWindshieldFrontAndWiperAndDrop|Automotive|0
windshield.front.and.wiper.and.spray|SFWindshieldFrontAndWiperAndSpray|Automotive|0
windshield.front.and.wiper.exclamationmark|SFWindshieldFrontAndWiperExclamationmark|Automotive|0
windshield.front.and.wiper.intermittent|SFWindshieldFrontAndWiperIntermittent|Automotive|0
windshield.front.and.wiper|SFWindshieldFrontAndWiper|Automotive|0
windshield.rear.and.fluid.and.spray|SFWindshieldRearAndFluidAndSpray|Automotive|0
windshield.rear.and.heat.waves|SFWindshieldRearAndHeatWaves|Automotive|0
windshield.rear.and.spray|SFWindshieldRearAndSpray|Automotive|0
windshield.rear.and.wiper.and.drop|SFWindshieldRearAndWiperAndDrop|Automotive|0
windshield.rear.and.wiper.and.spray|SFWindshieldRearAndWiperAndSpray|Automotive|0
windshield.rear.and.wiper.exclamationmark|SFWindshieldRearAndWiperExclamationmark|Automotive|0
windshield.rear.and.wiper.intermittent|SFWindshieldRearAndWiperIntermittent|Automotive|0
windshield.rear.and.wiper|SFWindshieldRearAndWiper|Automotive|0
wineglass.fill|SFWineglassFill|Objects & Tools|0
wineglass|SFWineglass|Objects & Tools|0
wonsign.arrow.trianglehead.counterclockwise.rotate.90|SFWonsignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
wonsign.bank.building.fill|SFWonsignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
wonsign.bank.building|SFWonsignBankBuilding|Commerce,Objects & Tools|0
wonsign.circle.fill|SFWonsignCircleFill|Commerce,Indices,Multicolor|0
wonsign.circle|SFWonsignCircle|Commerce,Draw,Indices,Variable|0
wonsign.gauge.chart.lefthalf.righthalf|SFWonsignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
wonsign.gauge.chart.leftthird.topthird.rightthird|SFWonsignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
wonsign.ring.dashed|SFWonsignRingDashed|Commerce,Home,Variable|0
wonsign.ring|SFWonsignRing|Commerce,Draw,Home|0
wonsign.square.fill|SFWonsignSquareFill|Commerce,Indices,Multicolor|0
wonsign.square|SFWonsignSquare|Commerce,Draw,Indices|0
wonsign|SFWonsign|Commerce,Indices|0
wrench.adjustable.fill|SFWrenchAdjustableFill|Objects & Tools|0
wrench.adjustable|SFWrenchAdjustable|Objects & Tools|0
wrench.and.screwdriver.fill|SFWrenchAndScrewdriverFill|Objects & Tools|0
wrench.and.screwdriver|SFWrenchAndScrewdriver|Objects & Tools|0
wrongwaysign.fill|SFWrongwaysignFill|Automotive,Multicolor|0
wrongwaysign|SFWrongwaysign|Automotive,Multicolor|0
x.circle.fill|SFXCircleFill|Gaming,Indices,Multicolor|0
x.circle|SFXCircle|Draw,Gaming,Indices,Variable|0
x.square.fill|SFXSquareFill|Indices,Multicolor|0
x.square|SFXSquare|Draw,Indices|0
xbox.logo|SFXboxLogo|Gaming|1
xmark.app.fill|SFXmarkAppFill|Draw,Multicolor|0
xmark.app|SFXmarkApp|Draw,Multicolor|0
xmark.bin.circle.fill|SFXmarkBinCircleFill|Multicolor,Objects & Tools|0
xmark.bin.circle|SFXmarkBinCircle|Draw,Objects & Tools,Variable|0
xmark.bin.fill|SFXmarkBinFill|Draw,Multicolor,Objects & Tools|0
xmark.bin|SFXmarkBin|Draw,Objects & Tools|0
xmark.circle.badge.airplane.fill|SFXmarkCircleBadgeAirplaneFill|Multicolor,Transportation|0
xmark.circle.badge.airplane|SFXmarkCircleBadgeAirplane|Transportation|0
xmark.circle.fill|SFXmarkCircleFill|Draw,Gaming,Multicolor|0
xmark.circle|SFXmarkCircle|Draw,Gaming,Multicolor,Variable|0
xmark.diamond.fill|SFXmarkDiamondFill|Draw,Multicolor|0
xmark.diamond|SFXmarkDiamond|Draw,Multicolor|0
xmark.icloud.fill|SFXmarkIcloudFill|Connectivity,Draw,Multicolor|1
xmark.icloud|SFXmarkIcloud|Connectivity,Draw|1
xmark.octagon.fill|SFXmarkOctagonFill|Draw,Multicolor|0
xmark.octagon|SFXmarkOctagon|Draw,Multicolor|0
xmark.rectangle.fill|SFXmarkRectangleFill|Draw,Multicolor|0
xmark.rectangle.portrait.fill|SFXmarkRectanglePortraitFill|Draw,Multicolor|0
xmark.rectangle.portrait|SFXmarkRectanglePortrait|Draw,Multicolor|0
xmark.rectangle|SFXmarkRectangle|Draw,Multicolor|0
xmark.seal.fill|SFXmarkSealFill|Draw,Multicolor,Privacy & Security|0
xmark.seal|SFXmarkSeal|Draw,Privacy & Security|0
xmark.shield.fill|SFXmarkShieldFill|Draw,Multicolor,Objects & Tools,Privacy & Security|0
xmark.shield|SFXmarkShield|Draw,Multicolor,Objects & Tools,Privacy & Security|0
xmark.square.fill|SFXmarkSquareFill|Draw,Multicolor|0
xmark.square|SFXmarkSquare|Draw,Multicolor|0
xmark|SFXmark|Draw,Gaming,Multicolor|0
xmark.triangle.circle.square.fill|SFXmarkTriangleCircleSquareFill|Accessibility,Shapes|0
xmark.triangle.circle.square|SFXmarkTriangleCircleSquare|Accessibility,Shapes|0
xserve.raid|SFXserveRaid|Devices|1
xserve|SFXserve|Devices|1
y.circle.fill|SFYCircleFill|Gaming,Indices,Multicolor|0
y.circle|SFYCircle|Draw,Gaming,Indices,Variable|0
y.square.fill|SFYSquareFill|Indices,Multicolor|0
y.square|SFYSquare|Draw,Indices|0
yensign.arrow.trianglehead.counterclockwise.rotate.90|SFYensignArrowTriangleheadCounterclockwiseRotate90|Arrows,Commerce,Draw|0
yensign.bank.building.fill|SFYensignBankBuildingFill|Commerce,Multicolor,Objects & Tools|0
yensign.bank.building|SFYensignBankBuilding|Commerce,Objects & Tools|0
yensign.circle.fill|SFYensignCircleFill|Commerce,Indices,Multicolor|0
yensign.circle|SFYensignCircle|Commerce,Draw,Indices,Variable|0
yensign.gauge.chart.lefthalf.righthalf|SFYensignGaugeChartLefthalfRighthalf|Commerce,Home,Variable|0
yensign.gauge.chart.leftthird.topthird.rightthird|SFYensignGaugeChartLeftthirdTopthirdRightthird|Commerce,Home,Variable|0
yensign.ring.dashed|SFYensignRingDashed|Commerce,Home,Variable|0
yensign.ring|SFYensignRing|Commerce,Draw,Home|0
yensign.square.fill|SFYensignSquareFill|Commerce,Indices,Multicolor|0
yensign.square|SFYensignSquare|Commerce,Draw,Indices|0
yensign|SFYensign|Commerce,Indices|0
yieldsign.fill|SFYieldsignFill|Automotive,Multicolor|0
yieldsign|SFYieldsign|Automotive,Multicolor|0
z.circle.fill|SFZCircleFill|Gaming,Indices,Multicolor|0
z.circle|SFZCircle|Draw,Gaming,Indices,Variable|0
z.square.fill|SFZSquareFill|Indices,Multicolor|0
z.square|SFZSquare|Draw,Indices|0
zipper.page|SFZipperPage||0
zl.button.roundedtop.horizontal.fill|SFZlButtonRoundedtopHorizontalFill|Gaming,Multicolor|0
zl.button.roundedtop.horizontal|SFZlButtonRoundedtopHorizontal|Gaming|0
zr.button.roundedtop.horizontal.fill|SFZrButtonRoundedtopHorizontalFill|Gaming,Multicolor|0
zr.button.roundedtop.horizontal|SFZrButtonRoundedtopHorizontal|Gaming|0
zzz|SFZzz||0
    """.trimIndent()

    public val all: List<SfSymbolMetadata> by lazy {
        raw.lineSequence().map { line ->
            val parts = line.split('|')
            val categories = if (parts[2].isEmpty()) emptyList() else parts[2].split(',')
            SfSymbolMetadata(
                appleName = parts[0],
                pascalName = parts[1],
                categories = categories,
                isRestricted = parts[3] == "1"
            )
        }.toList()
    }

    private val nameIndex: Map<String, SfSymbolMetadata> by lazy {
        all.associateBy { it.pascalName }
    }

    private val appleNameIndex: Map<String, SfSymbolMetadata> by lazy {
        all.associateBy { it.appleName }
    }

    public fun findByPascalName(name: String): SfSymbolMetadata? = nameIndex[name]

    public fun findByAppleName(name: String): SfSymbolMetadata? = appleNameIndex[name]

    public fun search(query: String): List<SfSymbolMetadata> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return all
        return all.filter {
            it.appleName.lowercase().contains(q) ||
            it.pascalName.lowercase().contains(q) ||
            it.categories.any { cat -> cat.lowercase().contains(q) }
        }
    }
}
