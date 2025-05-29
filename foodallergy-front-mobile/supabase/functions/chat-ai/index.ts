import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    const { message, userId, conversationId } = await req.json()

    // Initialize Supabase client
    const supabase = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_ANON_KEY') ?? ''
    )

    // Get user's allergies for context
    const { data: userAllergies } = await supabase
      .from('user_allergens')
      .select(`
        allergens (
          name,
          description,
          keywords
        )
      `)
      .eq('user_id', userId)

    // Build context
    const allergyContext = userAllergies?.map(ua => ua.allergens.name).join(', ') || 'No allergies configured'

    // Generate AI response (using OpenAI, Hugging Face, or simple rule-based)
    const aiResponse = await generateAIResponse(message, allergyContext)

    // Save conversation to Supabase
    const { data: conversation } = await supabase
      .from('chat_conversations')
      .upsert({
        id: conversationId,
        user_id: userId,
        updated_at: new Date().toISOString()
      })
      .select()
      .single()

    // Save messages
    await supabase.from('chat_messages').insert([
      {
        conversation_id: conversation.id,
        message_text: message,
        is_user_message: true
      },
      {
        conversation_id: conversation.id,
        message_text: aiResponse,
        is_user_message: false
      }
    ])

    return new Response(
      JSON.stringify({
        conversationId: conversation.id,
        botResponse: aiResponse,
        success: true
      }),
      {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 200,
      },
    )
  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 400,
      },
    )
  }
})

async function generateAIResponse(message: string, allergyContext: string): Promise<string> {
  // Simple rule-based responses for now
  const lowerMessage = message.toLowerCase()

  if (lowerMessage.includes('allerg')) {
    return `Based on your allergies (${allergyContext}), I can help you check if foods are safe. What specific food or ingredient would you like me to check?`
  }

  if (lowerMessage.includes('scan')) {
    return "To scan a product: 1) Go to the Scanner tab, 2) Point your camera at the barcode, 3) Wait for the scan to complete. I'll automatically check it against your allergies!"
  }

  if (lowerMessage.includes('ingredient')) {
    return "I can help you understand ingredients! Share the ingredient list with me, and I'll identify any potential allergens based on your profile."
  }

  return "I'm here to help with food allergies! I can help you scan products, understand ingredients, or check if foods are safe for your allergies. What would you like to know?"
}