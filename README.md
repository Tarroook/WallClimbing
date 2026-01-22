# Wall-Climbing

## Features

Wall-Climbing adds the capability to climb on any walls for players at the cost of your stamina.

To climb you have to face a solid block and tilt your head slightly in the direction where you want to climb when in mid-air. The easiest way to do that is to just jump at a wall while looking at it, if you want to stop climbing simply look away from the wall (eventually will be tied to inputs such as holding jump when I figure out how to do that).

Climbing consumes stamina very quickly so you should not be able to climb for long (meant to not make it too strong, eventually should be configurable).



## Early Access

This mod is still in development so bugs are expected.

Here is a little roadmap of things I wish to add or change:

Can dos:

- Make stamina drain vary depending on if you are actively climbing or just holding still against a wall
- Slow you down when you're low on stamina
- Config Options
- Items that make you climb longer and faster (low priority because I want to flesh out the climbing itself first, which would mean I would need to do the things listed below)

Can't currently because of modding limitations but want to:

- WASD Inputs to control climb direction (They are nowehere to be found and used in the codebase, my running theory is that movement is handled on the client side)
- Hold and release space to control when you want to start and stop climbing (Same issue as for WASD)
- Make it play the climbing animations like when you are on a ladder (Also same issue, I think animations for the movement of the player and handling of the various movement states is done client side as well)
