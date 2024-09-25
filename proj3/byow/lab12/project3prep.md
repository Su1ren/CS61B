# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: Decompose the issue into tessellation size and hexagon size. Anchor the starting position, radiate towards right up side and bottom down side.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: Build up the stage for rooms and hallways. 

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: Compute the positions of hexagons, draw a sketch and understand.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: The hallway connects rooms, former long and thin, latter wide and big.
