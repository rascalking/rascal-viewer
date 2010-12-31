// rendermonkey default textured phong vertex shader
uniform mat4 matViewProjectionInverseTranspose;
uniform mat4 matViewProjection;
uniform vec3 fvLightPosition;
uniform vec3 fvEyePosition;

attribute vec4 rm_Vertex;
attribute vec4 rm_Normal;
attribute vec2 rm_TexCoord0;

varying vec2 Texcoord;
varying vec3 ViewDirection;
varying vec3 Normal;

void main( void )
{
   gl_Position = matViewProjection * rm_Vertex;
   Texcoord    = rm_TexCoord0.xy;
    
   vec4 fvObjectPosition = matViewProjection * rm_Vertex;
   
   ViewDirection  = fvEyePosition - fvObjectPosition.xyz;
   Normal         = (matViewProjectionInverseTranspose * rm_Normal).xyz;
   
}